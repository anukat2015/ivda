package sk.stuba.fiit.perconik.ivda.server;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessAllEvents;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeCheckinEventDto;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public final class TimelineServlet extends DataSourceServlet {
    private static final Logger logger = Logger.getLogger(TimelineServlet.class.getName());

    /**
     * Spracuj parametre ziadosti. Nasledne generuj tabulku.
     *
     * @param query
     * @param req
     * @return
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest req) {
        // Pohyb okna nema vplyv na zmenu datumu, cize tensie okno zobrazuje to iste len ide o responzivny dizajn
        // Vplyv na rozsah ma jedine  zoom
        // Cize musime vypocitat sirku okna a poslat to sem
        GregorianCalendar start, end;
        Integer width;
        try {
            start = DateUtils.fromString(req.getParameter("start"));
            end = DateUtils.fromString(req.getParameter("end"));
            width = Integer.valueOf(req.getParameter("width"));
            //start = DateUtils.fromString("2014-03-01T08:00:00.000Z");
            //end = DateUtils.fromString("2014-10-30T16:00:00.000Z");
            width = Integer.valueOf(1012);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return generateDataTable(start, end, width);
    }

    /**
     * Generuj datovu tabulku pre klienta na zaklade ziadosti.
     *
     * @param start
     * @param end
     * @param width
     * @return
     */
    protected DataTable generateDataTable(GregorianCalendar start, GregorianCalendar end, Integer width) {

        EventsRequest request = new EventsRequest();
        ProcessEventsToDataTable process;

        start = DateUtils.fromString("2013-03-01T08:00:00.000Z");
        end = DateUtils.fromString("2014-10-03T16:00:00.000Z");
        request.setTime(start, end);
        //request.setType(new IdeCodeEventDto(), "pastefromweb");
        request.setType(new IdeCheckinEventDto());
        process = new ProcessAllEvents(request);


        //start = DateUtils.fromString("2014-08-06T01:00:00.000Z");
        //end = DateUtils.fromString("2014-08-06T23:00:00.000Z");
        //request.setTime(start, end);
        //process = new ProcessWebTabEvents(request);


        // PinballFX2 v procesoch :D
//        start = DateUtils.fromString("2014-08-06T01:00:00.000Z");
//        end = DateUtils.fromString("2014-08-06T23:00:00.000Z");
//        request.setTime(start, end).setType(new ProcessesChangedSinceCheckEventDto());
//        process = new ProcessAllProcess(request);

        // udalost MonitoringStartedEventDto nastane tak raz za tyzden, raz za tyzden zapina pc?
        // dokaz http://perconik.fiit.stuba.sk/UserActivity/api/useractivity?page=0&pagesize=100&timefrom=2014-08-03T01:00:00Z&timeTo=2014-08-06T23:00:00Z&EventTypeUri=http://perconik.gratex.com/useractivity/event/system/monitoringstarted&ExactType=true


        // pzbell vymazal 8suborov, 8suborov pridal a potom cely vecer sa menili len procesi
//        start = DateUtils.fromString("2014-07-03T00:00:01.000Z");
//        end = DateUtils.fromString("2014-07-03T23:59:00.000Z");
//        request.setTime(start, end).setUser("steltecia\\pzbell");
//        process = new ProcessJobEvents(request);

        //start = DateUtils.fromString("2014-07-03T00:00:01.000");
        //end = DateUtils.fromString("2014-07-03T10:59:00.000");
        //request.setTime(start, end).setUser("steltecia\\pzbell"); //.setType(new ProcessesChangedSinceCheckEventDto());
        //process = new ProcessAsGroup(request);

        logger.info("Start: " + DateUtils.toString(start) +
                " end:" + DateUtils.toString(end) +
                " width:" + width);
        process.start();
        return process.getDataTable();
    }

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}