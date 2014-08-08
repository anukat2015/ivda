package sk.stuba.fiit.perconik.ivda.uaca.client;

import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.Configuration;
import sk.stuba.fiit.perconik.ivda.DateUtils;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class EventsRequest implements Serializable {
    public UriBuilder builder; // stara sa  encodovanie

    public EventsRequest() {
        builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
        builder.path("useractivity");
        builder.queryParam("page", 0);
        builder.queryParam("pagesize", 100);
        builder.queryParam("ascending", true);  // pre spravne hladanie procesov
    }

    public URI getURI() {
        return builder.build();
    }

    public EventsRequest setTime(GregorianCalendar from, GregorianCalendar to) {
        builder.queryParam("timefrom", DateUtils.toString(from));
        builder.queryParam("timeTo", DateUtils.toString(to));
        return this;
    }

    public EventsRequest setType(URI EventTypeUri) {
        try {
            String encode = URLEncoder.encode(EventTypeUri.toString(), "UTF-8");
            builder.queryParam("EventTypeUri", encode);
            builder.queryParam("ExactType", true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public EventsRequest setWorkstation(String Workstation) {
        builder.queryParam("Workstation", Workstation);
        return this;
    }

    public EventsRequest setType(EventDto event) {
        return setType(UriBuilder.fromUri(event.getEventTypeUri()).build());
    }

    public EventsRequest setType(EventDto event, String subtype) {
        return setType(UriBuilder.fromUri(event.getEventTypeUri()).path(subtype).build());
    }

    public EventsRequest setUser(String user) {
        builder.queryParam("user", user);
        return this;
    }
}
/*
Attributes

Page [int]
Zero based index of the results page
PageSize [int]
Demanded size of the results page. Default page size is 20 items. Limited by server configuration to 100 items
TimeFrom [nullable DateTime]
Search for events with Timestamp later than TimeFrom
TimeTo [nullable DateTime]
Search for events with Timestamp earlier than TimeTo
EventTypeUri [string]
Search for events of types defined by URIs, which start with EventTypeUri - allows hierarchical type filtering
ExactType [bool]
Search for events of type defined by exact URI. Default is false
User [string]
Search for events from users with names starting with given string
Workstation [string]
Search for events from workstations with names starting with given string
Ascending [bool]
Defines results order. Results are ordered by Event Timestamp.
Default is false, which means results are ordered descending or from the newest to the oldest events
If true, results are ordered ascending or from the oldest to the newest events
*/