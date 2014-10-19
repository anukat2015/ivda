package sk.stuba.fiit.perconik.ivda.activity.client;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.cache.CompositeGuavaCache;
import sk.stuba.fiit.perconik.ivda.util.cache.ofy.OfyDynamicCache;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;
import sk.stuba.fiit.perconik.ivda.util.rest.WebClient;

import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
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
    public ImmutableList<EventDto> getEvents(EventsRequest request) {
        try {
            // Vrat prazdny vysledok ked:
            // poziadavka smeruje na vyber eventov z buducnosti
            // poziadavka smeruje na vyber eventov za poslednu hodinu
            Date timeTo = DateUtils.fromString(request.getTimeTo());
            Date now = DateUtils.getNow();
            long diff = DateUtils.diff(timeTo, now);
            if (diff >= IGNORE_CACHE_TIME.toMillis(1)) {
                UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
                URI uri = UriUtils.addBeanProperties(builder, request).build();
                return (ImmutableList<EventDto>) cache.get(uri);
            }
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
        return ImmutableList.of();
    }

    private static class SingletonHolder {
        public static final ActivityService INSTANCE = new ActivityService();
    }

    private final class ActivityCache extends CompositeGuavaCache<URI, Serializable> {
        public ActivityCache() {
            super(new OfyDynamicCache<URI, Serializable>() {
                @Override
                public ImmutableList<EventDto> valueNotFound(URI key) {
                    return downloadAll(key, EventsResponse.class, "pageIndex");
                }
            });
        }
    }
}
