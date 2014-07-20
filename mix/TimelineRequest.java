package sk.stuba.fiit.perconik.ivda.servlets;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.DateParameter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by Seky on 13. 7. 2014.
 */


@Path("/timeline")
public class TimelineRequest {

    private static final Logger logger = Logger.getLogger(TimelineRequest.class.getName());

    @GET
    @Produces("application/json")
    public String getClichedMessage(@QueryParam("start") DateParameter start, @QueryParam("end") DateParameter end) {
        logger.info("Start: " + start + " end:" + end);

        return "Hello World";
    }

}
