package sk.stuba.fiit.perconik.ivda.uaca.client;

import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda ktora namapuje EventsURI vlastnosti a ulozi ich do URI.
 */
public final class EventsRequest extends EventsURI {
    private final UriBuilder builder; // stara sa  encodovanie

    public EventsRequest() {
        builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
    }

    public URI getURI() {
        return UriUtils.addBeanProperties(builder, EventsURI.class, this).build();
    }


    public EventsRequest setTime(GregorianCalendar from, GregorianCalendar to) {
        timeFrom = DateUtils.toString(from);
        timeTo = DateUtils.toString(to);
        return this;
    }

    public EventsRequest setType(URI EventTypeUri) {
        eventTypeUri = EventTypeUri.toString();
        exactType = true;
        return this;
    }

    public EventsRequest setType(EventDto event) {
        return setType(event.getEventTypeUri());
    }

    public EventsRequest setType(EventDto event, String subtype) {
        return setType(UriBuilder.fromUri(event.getEventTypeUri()).path(subtype).build());
    }
}