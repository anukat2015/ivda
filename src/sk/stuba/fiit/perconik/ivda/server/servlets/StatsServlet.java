package sk.stuba.fiit.perconik.ivda.server.servlets;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebNavigateEventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.grouping.PerWebDividing;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.server.processevents.Array2Json;
import sk.stuba.fiit.perconik.ivda.server.processevents.BrowserVsWrittenCode;
import sk.stuba.fiit.perconik.ivda.server.processevents.CountEventsHistogram;
import sk.stuba.fiit.perconik.ivda.server.processevents.CreateBaseActivities;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramByHashTable;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramBySiblings;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;
import sk.stuba.fiit.perconik.ivda.util.lang.TimeGranularity;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
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
                case "activityLocDomainVisitsGrouped ": {
                    activityLocDomainVisitsGrouped(events, request.getGranularity(), stream);
                    break;
                }
                case "activityTimeGrouped": {
                    activityTimeGrouped(events, request.getGranularity(), stream);
                    break;
                }
                case "activityHistogram": {
                    activityHistogram(events, stream);
                    break;
                }
                case "activityDetail": {
                    activityHistogram(events, stream);
                    break;
                }
                case "domainVisits": {
                    domainVisits(events, stream);
                    break;
                }
                case "fileModifications": {
                    fileModifications(events, stream);
                    break;
                }
                case "loc": {
                    locChanges(events, request.getGranularity(), stream);
                    break;
                }
                case "countEvents": {
                    countEvents(events, request.getGranularity(), stream);
                    break;
                }
                case "webDuration": {
                    webDuration(events, request.getGranularity(), stream);
                    break;
                }
                case "browserVsRewrittenCode": {
                    browserVsRewrittenCode(events, stream);
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

    private static void domainVisits(Iterator<EventDto> events, ServletOutputStream stream) throws IOException {
        // Vypocitaj histogram udaje
        final Histogram<String> histogram = new HistogramByHashTable<>();
        ProcessIterator process = new ProcessIterator<EventDto>() {
            @Override
            protected void proccessItem(EventDto event) {
                if (event instanceof WebNavigateEventDto) {
                    WebNavigateEventDto cevent = (WebNavigateEventDto) event;
                    histogram.map(cevent.getDomain());
                }
            }
        };
        process.proccess(events);

        Array2Json json = new Array2Json(stream);
        json.start();
        Collection<Map.Entry<String, MutableInt>> zoznam = histogram.reduce(true, false, false);
        for (Map.Entry<String, MutableInt> entry : zoznam) {
            IvdaEvent e = new IvdaEvent();
            e.setContent(entry.getKey());
            e.setY(entry.getValue().toInteger());
            json.write(e);
        }
        json.close();
    }

    private static void fileModifications(Iterator<EventDto> events, ServletOutputStream stream) throws IOException {
        // Vypocitaj histogram udaje
        final Histogram<String> histogram = new HistogramByHashTable<>();
        ProcessIterator process = new ProcessIterator<EventDto>() {
            @Override
            protected void proccessItem(EventDto event) {
                if (event instanceof IdeCodeEventDto) {
                    IdeCodeEventDto cevent = (IdeCodeEventDto) event;
                    histogram.map(cevent.getDocument().getServerPath());
                }
            }
        };
        process.proccess(events);

        Array2Json json = new Array2Json(stream);
        json.start();
        Collection<Map.Entry<String, MutableInt>> zoznam = histogram.reduce(true, false, false);
        for (Map.Entry<String, MutableInt> entry : zoznam) {
            IvdaEvent e = new IvdaEvent();
            e.setContent(entry.getKey());
            e.setY(entry.getValue().toInteger());
            json.write(e);
        }
        json.close();
    }

    private static void countEvents(Iterator<EventDto> events, TimeGranularity g, ServletOutputStream stream) throws IOException {
        // Vypocitaj histogram udaje
        CountEventsHistogram histogram = new CountEventsHistogram(g);
        histogram.proccess(events);
        Collection<Map.Entry<Date, MutableInt>> zoznam = histogram.getHistogram().reduce();

        // Posli udaje do vystupu
        Array2Json json = new Array2Json(stream);
        json.start();
        flushMapEntry(json, zoznam, g, "events");
        json.close();
    }

    private static void locChanges(Iterator<EventDto> events, TimeGranularity g, ServletOutputStream stream) throws IOException {
        // Posli udaje do vystupu
        final Histogram<Date> histogram = new HistogramBySiblings<>();
        Array2Json json = new Array2Json(stream);
        json.start();
        while (events.hasNext()) {
            EventDto event = events.next();
            if (event instanceof IdeCodeEventDto) {
                IdeCodeEventDto codeEvent = (IdeCodeEventDto) event;
                int loc = EventsUtil.codeWritten(codeEvent.getText());
                if (g.compareTo(TimeGranularity.PER_VALUE) != 0) {
                    Date date = g.roundDate(event.getTimestamp());
                    histogram.map(date, loc);
                } else {
                    // Bud sa udaje ulozia do histogramu alebo sa rovno vypisu
                    IvdaEvent e = new IvdaEvent();
                    e.setStart(event.getTimestamp());
                    e.setGroup("events");
                    e.setY(loc);
                    json.write(e);
                }
            }
        }
        Collection<Map.Entry<Date, MutableInt>> zoznam = histogram.reduce();
        flushMapEntry(json, zoznam, g, "events");
        json.close();
    }

    private static void activityHistogram(Iterator<EventDto> events, ServletOutputStream stream) throws IOException {
        final Array2Json out = new Array2Json(stream);
        ProcessIterator<EventDto> process = new CreateBaseActivities() {
            @Override
            protected void foundEndOfGroup(Group group) {
                // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
                EventDto first = group.getFirstEvent();
                EventDto last = group.getLastEvent();

                // Store event
                IvdaEvent event = new IvdaEvent();
                event.setStart(first.getTimestamp());
                event.setEnd(last.getTimestamp());
                event.setGroup(EventsUtil.event2name(first));

                Integer y = 0;
                if (group instanceof WebGroup) {
                    y = ((WebGroup) group).getVisitedLinks();
                } else if (group instanceof IdeGroup) {
                    y = ((IdeGroup) group).getLoc();
                }

                event.setY(y);
                out.write(event);
            }
        };

        out.start();
        process.proccess(events);
        out.close();
    }

    private static void activityTimeGrouped(Iterator<EventDto> events, final TimeGranularity granularity, ServletOutputStream stream) throws IOException {
        // Histogram pre kazdy sledovany atribut
        final Histogram<Date> webHistogram = new HistogramBySiblings<>();
        final Histogram<Date> ideHistogram = new HistogramBySiblings<>();

        // Iterujeme cez aktivity
        ProcessIterator<EventDto> process = new CreateBaseActivities() {
            @Override
            protected void foundEndOfGroup(Group group) {
                // Podla typu zapis do atributy
                EventDto first = group.getFirstEvent();
                Date date = granularity.roundDate(first.getTimestamp());

                int y = (int) group.getTimeDiff();
                if (group instanceof WebGroup) {
                    webHistogram.map(date, y);
                } else if (group instanceof IdeGroup) {
                    ideHistogram.map(date, y);
                }
            }
        };
        process.proccess(events);

        // Posli udaje do vystupu
        Collection<Map.Entry<Date, MutableInt>> zoznam;
        Array2Json json = new Array2Json(stream);
        json.start();
        zoznam = webHistogram.reduce(false, false, false);
        flushMapEntry(json, zoznam, granularity, "Web");
        zoznam = ideHistogram.reduce(false, false, false);
        flushMapEntry(json, zoznam, granularity, "Ide");
        json.close();
    }

    private static void activityLocDomainVisitsGrouped(Iterator<EventDto> events, final TimeGranularity granularity, ServletOutputStream stream) throws IOException {
        // Histogram pre kazdy sledovany atribut
        final Histogram<Date> webHistogram = new HistogramBySiblings<>();
        final Histogram<Date> ideHistogram = new HistogramBySiblings<>();

        // Iterujeme cez aktivity
        ProcessIterator<EventDto> process = new CreateBaseActivities() {
            @Override
            protected void foundEndOfGroup(Group group) {
                // Podla typu zapis do atributy
                EventDto first = group.getFirstEvent();
                Date date = granularity.roundDate(first.getTimestamp());

                int y;
                if (group instanceof WebGroup) {
                    y = ((WebGroup) group).getVisitedLinks();
                    webHistogram.map(date, y);
                } else if (group instanceof IdeGroup) {
                    y = ((IdeGroup) group).getLoc();
                    ideHistogram.map(date, y);
                }
            }
        };
        process.proccess(events);

        // Posli udaje do vystupu
        Collection<Map.Entry<Date, MutableInt>> zoznam;
        Array2Json json = new Array2Json(stream);
        json.start();
        zoznam = webHistogram.reduce(false, false, false);
        flushMapEntry(json, zoznam, granularity, "Web");
        zoznam = ideHistogram.reduce(false, false, false);
        flushMapEntry(json, zoznam, granularity, "Ide");
        json.close();
    }

    private static void flushMapEntry(Array2Json json, Collection<Map.Entry<Date, MutableInt>> zoznam, TimeGranularity g, String group) {
        for (Map.Entry<Date, MutableInt> entry : zoznam) {
            Date start = entry.getKey();
            IvdaEvent e = new IvdaEvent();
            e.setStart(start);
            e.setGroup(group);
            e.setEnd(g.increment(start));
            e.setY(entry.getValue().toInteger());
            json.write(e);
        }
    }

    private static void webDuration(Iterator<EventDto> events, TimeGranularity g, ServletOutputStream stream) throws IOException {
        // Histogram pre kazdy sledovany atribut
        final Histogram<String> histogram = new HistogramByHashTable<>();

        // Iterujeme cez aktivity
        ProcessIterator<EventDto> process = new CreateBaseActivities() {
            @Override
            protected void started() {
                super.started();
                divide = new PerWebDividing(); //chceme delenie klasicke + domeny
            }

            @Override
            protected void foundEndOfGroup(Group group) {
                if (!(group instanceof WebGroup)) {
                    return;
                }

                WebEventDto web = (WebEventDto) group.getFirstEvent();
                String url = web.getDomain();
                if (url == null) {
                    return;
                }
                histogram.map(url, (int) group.getTimeDiff());
            }
        };
        process.proccess(events);

        // Zapiseme vysledok
        Array2Json json = new Array2Json(stream);
        json.start();
        Collection<Map.Entry<String, MutableInt>> zoznam = histogram.reduce(true, false, true);
        for (Map.Entry<String, MutableInt> entry : zoznam) {
            // Store event
            IvdaEvent event = new IvdaEvent();
            event.setContent(entry.getKey());
            event.setY(entry.getValue().toInteger() / (int) g.millis());
            json.write(event);
        }
        json.close();
    }

    private static void browserVsRewrittenCode(Iterator<EventDto> events, ServletOutputStream stream) throws IOException {
        final Array2Json out = new Array2Json(stream);

        ProcessIterator<EventDto> process = new BrowserVsWrittenCode() {
            @Override
            protected void foundActivitiesRelation() {
                // Store event
                IvdaEvent event = new IvdaEvent();
                event.setStart(webActivities.get(0).getFirstEvent().getTimestamp());
                event.setEnd(endOfLastIdeActivity());
                event.setContent(Long.toString(this.getTimeDuration()));
                event.setY((int) this.getWrittenCode());
                out.write(event);
            }
        };

        out.start();
        process.proccess(events);
        out.close();
    }

}
