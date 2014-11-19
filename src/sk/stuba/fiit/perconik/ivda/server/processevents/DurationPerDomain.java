package sk.stuba.fiit.perconik.ivda.server.processevents;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.BaseDividing;
import sk.stuba.fiit.perconik.ivda.server.grouping.ProcessAsGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.server.servlets.IvdaEvent;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramByHashTable;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Seky on 10. 11. 2014.
 */
public final class DurationPerDomain extends ProcessAsGroup {
    private static final Logger LOGGER = Logger.getLogger(DurationPerDomain.class.getName());

    private final Array2Json out;
    private final Histogram<String> domainDuration;

    public DurationPerDomain(OutputStream out) {
        this.out = new Array2Json(out);
        domainDuration = new HistogramByHashTable<>();
        divide = new BaseDividing() {
            @Override
            public boolean canDivide(Group group, EventDto actual) {    // Zapneme per domain filter
                return divideByTime(group, actual) || divideForWebTabSpendTime(group, actual) || divideByType(group, actual);
            }
        };
    }

    @Override
    protected Group createNewGroup(EventDto event) {
        if (event instanceof WebEventDto) {
            return new CreateBaseActivities.WebGroup(event);
        }
        return new BoundedGroup(event);
    }

    @Override
    protected void foundEndOfGroup(Group group) {
        if (!(group instanceof CreateBaseActivities.WebGroup)) {
            return;
        }

        EventDto first = group.getFirstEvent();
        try {
            WebEventDto web = (WebEventDto) first;
            String url = web.getUrl();
            if (url == null) {
                return;
            }
            url = new URL(url).getHost();
            domainDuration.map(url, (int) group.getTimeDiff());
        } catch (MalformedURLException e2) {
            // poskodena url ignorujeme
        }catch (Exception e) {
            LOGGER.warn("Event je zleho typu?");
        }
    }


    protected void flushOut() {
        // Flush out
        out.start();
        int roundTo = 1000 * 60;
        Collection<Map.Entry<String, MutableInt>> zoznam = domainDuration.reduce(true, false, true);
        for (Map.Entry<String, MutableInt> entry : zoznam) {
            // Store event
            IvdaEvent event = new IvdaEvent();
            event.setContent(entry.getKey());
            event.setY(entry.getValue().toInteger() / roundTo);
            out.write(event);
        }
        out.close();
    }

    @Override
    protected void finished() {
        super.finished();
        flushOut();
    }
}
