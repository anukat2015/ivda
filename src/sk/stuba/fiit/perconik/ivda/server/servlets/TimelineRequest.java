package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import difflib.Delta;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.entities.ActivityService;
import sk.stuba.fiit.perconik.ivda.server.FileVersionsUtil;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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

        EventDto event = ActivityService.getInstance().getEvent(id);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
