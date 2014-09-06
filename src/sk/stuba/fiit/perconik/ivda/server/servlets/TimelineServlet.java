package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessFileVersions;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by Seky on 17. 7. 2014.
 * <p>
 * Servlet pre TImeline.
 */
public final class TimelineServlet extends DataSourceServlet {
    private static final Logger LOGGER = Logger.getLogger(TimelineServlet.class.getName());
    private static final long serialVersionUID = 4252962999830460395L;
    private static final int CACHE_DURATION_IN_SECOND = 0; // 60 * 60 * 24 * 2; // 2 days

    /**
     * Spracuj parametre ziadosti. Nasledne generuj tabulku.
     *
     * @param query
     * @param req
     * @return
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest req) {
        TimelineRequest request;
        try {
            request = new TimelineRequest(req);
            LOGGER.info("Request: " + request);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        EventsRequest activityRequest = new EventsRequest();
        activityRequest.setTime(request.getStart(), request.getEnd());

        ProcessEventsToDataTable process = new ProcessFileVersions();
        process.setFilter(request);

        ActivityService.getInstance().getEvents(activityRequest, process);
        return process.getDataTable();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doGet(req, resp);
        setCacheHeaders(req, resp);
    }

    protected void setCacheHeaders(HttpServletRequest req, HttpServletResponse resp) {
        // Set cache for response
        if (CACHE_DURATION_IN_SECOND == 0) {
            // Cachovanie je vypnute
            return;
        }
        GregorianCalendar end;
        try {
            end = DateUtils.fromString(req.getParameter("end"));
        } catch (Exception e) {
            return; // Ak zadal hlupost necachuj
        }
        long offset = DateUtils.getNow().getTimeInMillis() - end.getTimeInMillis();
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

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}