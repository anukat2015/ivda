package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessCodeWritten;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;

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
     * Generuj datovu tabulku pre klienta na zaklade ziadosti.
     *
     * @param start
     * @param end
     * @param width
     * @return
     */
    private static DataTable generateDataTable(GregorianCalendar start, GregorianCalendar end, Integer width) {
        EventsRequest request = new EventsRequest();
        request.setTime(start, end);
        ProcessEventsToDataTable process;

        request.setUser("steltecia\\pzbell");
        request.setType(new IdeCodeEventDto());
        process = new ProcessCodeWritten(request);

        process.start();
        return process.getDataTable();
    }

    /**
     * Spracuj parametre ziadosti. Nasledne generuj tabulku.
     *
     * @param query
     * @param req
     * @return
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request) {
        // Pohyb okna nema vplyv na zmenu datumu, cize tensie okno zobrazuje to iste len ide o responzivny dizajn
        // Vplyv na rozsah ma jedine  zoom
        // Cize musime vypocitat sirku okna a poslat to sem
        GregorianCalendar start;
        GregorianCalendar end;
        Integer width;
        Integer step;
        Integer scale;
        //noinspection OverlyBroadCatchBlock
        try {
            start = DateUtils.fromString(request.getParameter("start"));
            end = DateUtils.fromString(request.getParameter("end"));
            width = Integer.valueOf(request.getParameter("width"));
            step = Integer.valueOf(request.getParameter("step"));
            scale = Integer.valueOf(request.getParameter("scale"));
            //start = DateUtils.fromString("2014-07-21T08:00:00.000Z");
            //end = DateUtils.fromString("2014-07-21T16:00:00.000Z");

            if (!start.before(end)) {
                throw new WebApplicationException("Start date is not before end.", Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        LOGGER.info("Start: " + DateUtils.toString(start) +
                " end:" + DateUtils.toString(end) +
                " step:" + step +
                " scale:" + scale +
                " width:" + width);
        return generateDataTable(start, end, width);
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