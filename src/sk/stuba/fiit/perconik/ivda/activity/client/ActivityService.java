package sk.stuba.fiit.perconik.ivda.activity.client;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.cache.OfyBlob;
import sk.stuba.fiit.perconik.ivda.util.cache.OfyCache;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;
import sk.stuba.fiit.perconik.ivda.util.rest.WebClient;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * HTTP client pre sluzbu UACA. Ktory stahnie prvu stranku, poskytne udaje, stiahne druhu stranu atd.
 * Client cachuje odpovede do CACHE_FOLDER zlozky.
 * <p/>
 */
public class ActivityService extends RestClient {
    private static final Logger LOGGER = Logger.getLogger(ActivityService.class.getName());
    private static final File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();
    private static final TimeUnit IGNORE_CACHE_TIME = TimeUnit.HOURS;

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
     * Stiahni zoznam vsetkych eventov.
     *
     * @param uri adresa ktora sa ma stiahnut
     */
    public List<EventDto> getEvents(EventsRequest request) {
        try {
            // Vrat prazdny vysledok ked:
            // poziadavka smeruje na vyber eventov z buducnosti
            // poziadavka smeruje na vyber eventov za poslednu hodinu
            GregorianCalendar timeTo = DateUtils.fromString(request.getTimeTo());
            GregorianCalendar now = DateUtils.getNow();
            long diff = DateUtils.diff(timeTo, now);
            if (diff >= IGNORE_CACHE_TIME.toMillis(1)) {
                UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
                URI uri = UriUtils.addBeanProperties(builder, request).build();
                return (ImmutableList<EventDto>) cache.get(Key.create(OfyBlob.class, uri.toString())).getData();
            }
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
        return Collections.emptyList();
    }

    private static class SingletonHolder {
        public static final ActivityService INSTANCE = new ActivityService();
    }
    /*
    private final class ActivityCache extends GuavaFilesCache<URI, ImmutableList<EventDto>> {
        protected ActivityCache() {
            super(new File(CACHE_FOLDER, "activity"));
        }

        @Override
        protected ImmutableList<EventDto> fileNotFound(URI key) {
            return (ImmutableList<EventDto>) downloadAll(key, EventsResponse.class, "page");
        }
    }
    */

    private final class ActivityCache extends OfyCache<Key<OfyBlob>, OfyBlob> {
        @Override
        protected OfyBlob valueNotFound(Key<OfyBlob> key) {
            ImmutableList<EventDto> list = null;
            try {
                list = (ImmutableList<EventDto>) downloadAll(new URI(key.getName()), EventsResponse.class, "page");
            } catch (URISyntaxException e) {
                LOGGER.error("chyba", e);
            }
            return new OfyBlob(key.getName(), list);
        }
    }
}
