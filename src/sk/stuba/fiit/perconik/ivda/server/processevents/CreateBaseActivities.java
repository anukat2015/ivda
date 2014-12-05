package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebNavigateEventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.grouping.ProcessAsGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;

/**
 * Created by Seky on 9. 11. 2014.
 */
public abstract class CreateBaseActivities extends ProcessAsGroup {

    public static class WebGroup extends BoundedGroup {
        private int visitedLinks = 0;

        public WebGroup(EventDto actual) {
            super(actual);
        }

        @Override
        public void add2Group(EventDto event) {
            if (event instanceof WebNavigateEventDto) {
                visitedLinks++;
            }
            super.add2Group(event);
        }

        public int getVisitedLinks() {
            return visitedLinks;
        }
    }

    public static class IdeGroup extends BoundedGroup {
        private int loc = 0;

        public IdeGroup(EventDto actual) {
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

        public int getLoc() {
            return loc;
        }
    }

    @Override
    protected Group createNewGroup(EventDto event) {
        if (event instanceof WebEventDto) {
            return new WebGroup(event);
        }
        if (event instanceof IdeEventDto) {
            return new IdeGroup(event);
        }
        return new BoundedGroup(event);
    }
}
