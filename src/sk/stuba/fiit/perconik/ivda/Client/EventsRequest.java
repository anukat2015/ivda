package sk.stuba.fiit.perconik.ivda.Client;

import sk.stuba.fiit.perconik.ivda.Configuration;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class EventsRequest {
    public UriBuilder builder;

    public EventsRequest() {
        builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
        builder.path("useractivity");
    }

    public URI getURI() {
        return builder.build();
    }

    public EventsRequest setParameters(Date from) {
        builder.queryParam("timefrom", "2014-04-16T12:00Z");
        builder.queryParam("page", 0);
        builder.queryParam("pagesize", 10);
        return this;
    }
}