package sk.stuba.fiit.perconik.ivda.server.servlets;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.server.processevents.ListOfEventsForTimeline;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public class IvdaServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(IvdaServlet.class.getName());
    private static final long serialVersionUID = -2486259178164233472L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            final IvdaRequest request = new IvdaRequest(req);
            LOGGER.info("Request: " + request);

            resp.setContentType(MediaType.APPLICATION_JSON);
            ServletOutputStream stream = resp.getOutputStream();
            Iterator<EventDto> events = BankOfChunks.getEvents(request.getStart(), request.getEnd(), request.getDeveloper());
            ProcessIterator<EventDto> process = new ListOfEventsForTimeline(stream);
            process.proccess(events);
            setCacheHeaders(request, resp);
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private static void setCacheHeaders(IvdaRequest req, HttpServletResponse resp) {
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
