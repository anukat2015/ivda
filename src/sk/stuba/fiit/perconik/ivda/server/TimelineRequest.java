package sk.stuba.fiit.perconik.ivda.server;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by Seky on 18. 8. 2014.
 */
@Path("/timeline")
public class TimelineRequest {

    private static final Logger LOGGER = Logger.getLogger(TimelineRequest.class.getName());

    @GET
    @Produces("application/json")
    public String getFileDiff(@QueryParam("path") String path, @QueryParam("version") Integer version) {
        LOGGER.info("path: " + path + " version:" + version);

        return "Hello World";
    }

}
