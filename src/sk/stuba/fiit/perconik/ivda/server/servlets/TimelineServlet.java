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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Seky on 17. 7. 2014.
 * <p>
 * Servlet pre TImeline.
 */
public final class TimelineServlet extends DataSourceServlet {
    private static final Logger LOGGER = Logger.getLogger(TimelineServlet.class.getName());
    private static final long serialVersionUID = 4252962999830460395L;

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

        LOGGER.info("Start: " + DateUtils.toString(start) +
                " end:" + DateUtils.toString(end) +
                " width:" + width);
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
        //noinspection OverlyBroadCatchBlock
        try {
            start = DateUtils.fromString(request.getParameter("start"));
            end = DateUtils.fromString(request.getParameter("end"));
            width = Integer.valueOf(request.getParameter("width"));
            start = DateUtils.fromString("2014-07-21T08:00:00.000Z");
            end = DateUtils.fromString("2014-07-21T16:00:00.000Z");
            width = 1012;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return generateDataTable(start, end, width);
    }

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}