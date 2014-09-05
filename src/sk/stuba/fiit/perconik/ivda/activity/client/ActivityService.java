package sk.stuba.fiit.perconik.ivda.activity.client;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.cache.GuavaFilesCache;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;
import sk.stuba.fiit.perconik.ivda.util.rest.WebClient;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * HTTP client pre sluzbu UACA. Ktory stahnie prvu stranku, poskytne udaje, stiahne druhu stranu atd.
 * Client cachuje odpovede do CACHE_FOLDER zlozky.
 * <p>
 */
public class ActivityService extends RestClient {
    private static final Logger LOGGER = Logger.getLogger(ActivityService.class.getName());
    private static final File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();

    private final ActivityCache cache;
    private final WebClient client;

    private ActivityService() {
        cache = new ActivityCache();
        client = new WebClient();
    }

    @SuppressWarnings("SameReturnValue")
    public static ActivityService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public EventDto getEvent(String uid) {
        return (EventDto) client.synchronizedRequest(apiLink().path(uid).build(), EventDto.class);
    }

    @Override
    protected UriBuilder apiLink() {
        return UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
    }

    /**
     * TODO: Neskor metoda moze pracovat asynchronne ...
     *
     * @param uri adresa ktora sa ma stiahnut
     */
    public void getEvents(EventsRequest request, IProcessPage<EventsResponse> process) {
        try {
            UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
            URI uri = UriUtils.addBeanProperties(builder, request).build();
            getAllPages(uri, EventsResponse.class, "page", process);
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
    }

    @Override
    protected Object downloadUri(URI uri, Class<?> type) {
        return cache.getCache().getUnchecked(uri);
    }

    private static class SingletonHolder {
        public static final ActivityService INSTANCE = new ActivityService();
    }

    private final class ActivityCache extends GuavaFilesCache<URI, EventsResponse> {
        protected ActivityCache() {
            super(new File(CACHE_FOLDER, "activity"));
        }

        @Override
        protected EventsResponse fileNotFound(URI key) {
            //noinspection unchecked
            return (EventsResponse) client.synchronizedRequest(key, EventsResponse.class);
        }
    }
}
