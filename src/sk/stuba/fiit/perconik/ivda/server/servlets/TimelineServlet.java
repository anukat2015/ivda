package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEvents2TimelineEvents;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessFileVersions;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public class TimelineServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TimelineServlet.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TimelineResponse response = new TimelineResponse();
        resp.setContentType(MediaType.APPLICATION_JSON);

        try {
            TimelineRequest request = new TimelineRequest(req);
            LOGGER.info("Request: " + request);

            EventsRequest activityRequest = new EventsRequest();
            activityRequest.setTime(request.getStart(), request.getEnd());

            ProcessEvents2TimelineEvents process = new ProcessFileVersions();
            process.setFilter(request);
            process.downloaded(ActivityService.getInstance().getEvents(activityRequest));
            response.setGroups(process.getData());

            ServletOutputStream stream = resp.getOutputStream();
            MAPPER.writeValue(stream, response);
            setCacheHeaders(request, resp);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    protected void setCacheHeaders(TimelineRequest req, HttpServletResponse resp) {
        Integer duration = Configuration.getInstance().getCacheResponseDuration();
        // Set cache for response
        if (duration == 0) {
            // Cachovanie je vypnute
            return;
        }
        long offset = DateUtils.getNow().getTime() - req.getEnd().getTime();
        if (offset > 1000 * 60 * 60) {
            // Suradnica END je v minulosti, minimalne o 1 hodinu posunut
            // tzv buducnost necachujeme a ani eventy za poslednu hodinu, lebo tie sa mozu spracovavat este
            long now = System.currentTimeMillis();
            resp.setHeader("Cache-Control", "max-age=" + duration); //HTTP 1.1
            resp.setHeader("Cache-Control", "must-revalidate");
            resp.setHeader("Last-Modified", Long.toString(now)); //HTTP 1.0
            resp.setDateHeader("Expires", now + duration * 1000);
        }
    }

}