package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEvents2TimelineEvents;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessFileVersions;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public class TimelineServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TimelineServlet.class.getName());
    private static final int CACHE_DURATION_IN_SECOND = 0; // 60 * 60 * 24 * 2; // 2 days
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
            response.setEvents(process.getData());

            ServletOutputStream stream = resp.getOutputStream();
            MAPPER.writeValue(stream, response);
            setCacheHeaders(request, resp);
        } catch (Exception e) {
            response.setStatus(e.getMessage());;
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity(response)
                            .build());
        }
    }

    protected void setCacheHeaders(TimelineRequest req, HttpServletResponse resp) {
        // Set cache for response
        if (CACHE_DURATION_IN_SECOND == 0) {
            // Cachovanie je vypnute
            return;
        }
        long offset = DateUtils.getNow().getTime() - req.getEnd().getTime();
        if (offset > 1000 * 60 * 60) {
            // Suradnica END je v minulosti, minimalne o 1 hodinu posunut
            // tzv buducnost necachujeme a ani eventy za poslednu hodinu, lebo tie sa mozu spracovavat este
            long now = System.currentTimeMillis();
            resp.setHeader("Cache-Control", "max-age=" + Long.toString(CACHE_DURATION_IN_SECOND)); //HTTP 1.1
            resp.setHeader("Cache-Control", "must-revalidate");
            resp.setHeader("Last-Modified", Long.toString(now)); //HTTP 1.0
            resp.setDateHeader("Expires", now + CACHE_DURATION_IN_SECOND * 1000);
        }
    }

}