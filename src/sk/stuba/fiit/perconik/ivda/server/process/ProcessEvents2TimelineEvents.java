package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
public abstract class ProcessEvents2TimelineEvents {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEvents2TimelineEvents.class.getName());
    protected TimelineRequest filter;
    private List<TimelineEvent> list;

    public ProcessEvents2TimelineEvents() {
        filter = null;
        list = new ArrayList<>();
    }

    public void downloaded(List<EventDto> list) {
        for (EventDto event : list) {
            filterItem(event);
        }
        finished();
    }

    protected abstract void proccessItem(EventDto event);

    protected void filterItem(EventDto event) {
        if (filter != null) {
            // Filter pre developerov
            if (!filter.containDeveloper(event.getUser())) {
                return;
            }
        }
        proccessItem(event);
    }

    public void setFilter(TimelineRequest filter) {
        this.filter = filter;
    }

    public void finished() {
    }

    protected void add(TimelineEvent event) {
        event.setGroup(Developers.getInstance().blackoutName(event.getGroup()));
        list.add(event);
    }

    public List<TimelineEvent> getList() {
        return Collections.unmodifiableList(list);
    }

    public void add(EventDto e, Object metadata) {
        TimelineEvent event = new TimelineEvent(
                e.getTimestamp(),
                Developers.getInstance().blackoutName(e.getUser()),
                EventsUtil.event2Classname(e),
                EventsUtil.event2name(e),
                null,
                metadata
        );
        list.add(event);
    }
}

