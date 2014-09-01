package sk.stuba.fiit.perconik.ivda.activity.entities;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.GuavaFilesCache;
import sk.stuba.fiit.perconik.ivda.activity.client.IProcessDownloaded;
import sk.stuba.fiit.perconik.ivda.activity.client.WebClient;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * HTTP client pre sluzbu UACA. Ktory stahnie prvu stranku, poskytne udaje, stiahne druhu stranu atd.
 * Client cachuje odpovede do CACHE_FOLDER zlozky.
 * <p>
 */
public class ActivityService {
    private static final Logger LOGGER = Logger.getLogger(ActivityService.class.getName());
    private static final File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();

    private final WebClient client;
    private final URI2EventsCache cache;

    private ActivityService() {
        client = new WebClient();
        cache = new URI2EventsCache();
    }

    @SuppressWarnings("SameReturnValue")
    public static ActivityService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Nullable
    private URI getNextURI(PagedResponse<?> response, URI uri) {
        // Stary sposob pomocou citania URL adresy v "next" policku v odpovedi nefunguje spravne
        if (!response.isHasNextPage()) {
            return null;
        }

        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        for (NameValuePair pair : pairs) {
            if ("page".equals(pair.getName())) {
                Integer actual = Integer.valueOf(pair.getValue()) + 1;
                return UriBuilder.fromUri(uri).replaceQueryParam("page", actual).build();
            }
        }

        LOGGER.error("Page key do not exist in URI.");
        return null;
    }

    public EventDto getEvent(String uid) {
        UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
        builder.path(uid);
        return (EventDto) client.synchronizedRequest(builder.build(), EventDto.class);
    }

    /**
     * TODO: Neskor metoda moze pracovat asynchronne ...
     *
     * @param uri adresa ktora sa ma stiahnut
     */
    public void getEvents(EventsRequest request, IProcessDownloaded<EventDto> process) {
        URI uri = null;
        try {
            uri = request.getURI();
            LOGGER.info("Starting downloading.");
            while (uri != null) {
                PagedResponse<EventDto> response = downloadUri(uri);
                if (response.getResultSet().isEmpty()) {
                    LOGGER.warn("Resultset is empty! Bad request?");
                }
                if (!process.isDownloaded(response)) {
                    LOGGER.info("Downloading canceled.");
                    process.cancelled();
                    break;
                }
                //noinspection ConstantConditions
                uri = getNextURI(response, uri);
            }
            LOGGER.info("Downloading finished.");
            process.finished();
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
    }

    private PagedResponse<EventDto> downloadUri(URI uri) {
        return cache.getCache().getUnchecked(uri);
    }

    private static class SingletonHolder {
        public static final ActivityService INSTANCE = new ActivityService();
    }

    private final class URI2EventsCache extends GuavaFilesCache<URI, EventsResponse> {
        private static final long serialVersionUID = -2771011404900728777L;

        protected URI2EventsCache() {
            super(CACHE_FOLDER);
        }

        @Override
        protected EventsResponse fileNotFound(URI uri) {
            //noinspection unchecked
            return (EventsResponse) client.synchronizedRequest(uri, EventsResponse.class);
        }
    }
}
