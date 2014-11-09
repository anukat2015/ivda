package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.grouping.ProcessAsGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.server.servlets.IvdaEvent;

import java.io.OutputStream;

/**
 * Created by Seky on 9. 11. 2014.
 */
public class ActivityStats extends ProcessAsGroup {
    private final Array2Json out;

    public ActivityStats(OutputStream out) {
        this.out = new Array2Json(out);
    }

    @Override
    protected void finished() {
        super.finished();
        out.close();
    }

    @Override
    protected void started() {
        out.start();
        super.started();
    }

    private static class WebGroup extends BoundedGroup {
        private int visitedLinks = 0;

        private WebGroup(EventDto actual) {
            super(actual);
        }

        @Override
        public void add2Group(EventDto event) {
            if (event instanceof WebEventDto) {
                visitedLinks++;
            }
            super.add2Group(event);
        }
    }

    private static class IdeGroup extends BoundedGroup {
        private int loc = 0;

        private IdeGroup(EventDto actual) {
            super(actual);
        }

        @Override
        public void add2Group(EventDto event) {
            if (event instanceof IdeCodeEventDto) {
                IdeCodeEventDto codeEvent = (IdeCodeEventDto) event;
                loc += EventsUtil.codeWritten(codeEvent.getText());
            }

            super.add2Group(event);
        }
    }

    @Override
    protected Group createNewGroup(EventDto event) {
        if (event instanceof WebEventDto) {
            return new WebGroup(event);
        }
        if (event instanceof WebEventDto) {
            return new IdeGroup(event);
        }
        return new BoundedGroup(event);
    }

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
            y = ((WebGroup) group).visitedLinks;
        }
        if (group instanceof IdeGroup) {
            y = ((IdeGroup) group).loc;
        }

        event.setY(y);
        out.write(event);
    }
}
