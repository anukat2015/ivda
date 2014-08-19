package sk.stuba.fiit.perconik.ivda.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import difflib.Delta;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.uaca.client.WebClient;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by Seky on 18. 8. 2014.
 */
@Path("/timeline")
public class TimelineRequest {
    private static final Logger LOGGER = Logger.getLogger(TimelineRequest.class.getName());

    @GET
    @Path("/filediff")
    @Produces({"application/json"})
    public String getFileDiff(
            @NotNull @QueryParam("path") String path,
            @NotNull @QueryParam("version") Integer version,
            @NotNull @QueryParam("old") Integer old
    ) {
        if (path == null || version == null || old == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("path / version / old parameter is mandatory")
                            .build()
            );
        }

        List<Delta> deltas = FileVersionsUtil.printDiff(path, version, old);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(deltas);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/event")
    @Produces({"application/json"})
    public String getEvent(
            @NotNull @QueryParam("id") String id
    ) {
        if (id == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("id parameter is mandatory")
                            .build()
            );
        }

        WebClient client = new WebClient();
        UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
        builder.path(id);
        EventDto event;
        event = (EventDto) client.synchronizedRequest(builder.build(), EventDto.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
