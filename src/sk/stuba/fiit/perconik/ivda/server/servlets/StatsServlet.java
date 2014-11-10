package sk.stuba.fiit.perconik.ivda.server.servlets;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.util.lang.TimeGranularity;
import sk.stuba.fiit.perconik.ivda.server.processevents.ActivityStats;
import sk.stuba.fiit.perconik.ivda.server.processevents.Array2Json;
import sk.stuba.fiit.perconik.ivda.server.processevents.ComputeHistogram;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Seky on 9. 11. 2014.
 */
public class StatsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(IvdaServlet.class.getName());
    private static final long serialVersionUID = -6246210501504519167L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            final StatsRequest request = new StatsRequest(req);
            LOGGER.info("Request: " + request);

            resp.setContentType(MediaType.APPLICATION_JSON);
            ServletOutputStream stream = resp.getOutputStream();
            Iterator<EventDto> events = BankOfChunks.getEvents(request.getStart(), request.getEnd(), request.getDeveloper());

            switch (request.getAttribute()) {
                case "activity": {
                    pohladNaAktivity(events, stream);
                    break;
                }
                case "count": {
                    pocetPrvkov(events, request.getGranularity(), stream);
                    break;
                }
                default: {
                    throw new Exception("Unknown attribute.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Reponse: ", e);
            resp.sendError(500, e.getMessage());
        }

    }

    private static void pocetPrvkov(Iterator<EventDto> events, TimeGranularity g, ServletOutputStream stream) throws IOException {
        // Vypocitaj histogram udaje
        ComputeHistogram histogram = new ComputeHistogram(g);
        histogram.proccess(events);
        Iterator<Map.Entry<Date, MutableInt>> data = histogram.getHistogram().reduce();

        // Posli udaje do vystupu
        Array2Json json = new Array2Json(stream);
        json.start();
        while (data.hasNext()) {
            Map.Entry<Date, MutableInt> entry = data.next();
            Date start = entry.getKey();

            IvdaEvent e = new IvdaEvent();
            e.setStart(start);
            e.setGroup("events");
            if (g.compareTo(TimeGranularity.PER_VALUE) != 0){
                e.setEnd(g.increment(start));
            }
            e.setY(entry.getValue().toInteger());
            json.write(e);
        }
        json.close();
    }

    private static void pohladNaAktivity(Iterator<EventDto> events, ServletOutputStream stream) throws IOException {
        // Vyfiltruj prvky pre pouzivatela
        ProcessIterator<EventDto> process = new ActivityStats(stream);
        process.proccess(events);
    }

}
