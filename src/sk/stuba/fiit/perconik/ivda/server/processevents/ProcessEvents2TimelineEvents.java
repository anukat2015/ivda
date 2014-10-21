package sk.stuba.fiit.perconik.ivda.server.processevents;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;

import java.util.*;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
public abstract class ProcessEvents2TimelineEvents {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEvents2TimelineEvents.class.getName());
    private Map<String, List<TimelineEvent>> list;

    public ProcessEvents2TimelineEvents() {
        list = new HashMap<>();
    }

    public void add(TimelineEvent event) {
        // Black out developer name
        String group = Developers.getInstance().blackoutName(event.getGroup());
        event.setGroup(group);

        // Check if exist ..
        List<TimelineEvent> events = list.get(group);
        if (events == null) {
            events = new ArrayList<>();
            list.put(group, events);
        }
        events.add(event);
    }

    public void downloaded(List<EventDto> list) {
        for (EventDto event : list) {
            filterItem(event);
        }
        finished();
    }

    protected abstract void proccessItem(EventDto event);

    protected void filterItem(EventDto event) {
        proccessItem(event);
    }

    public void finished() {
    }

    public Map<String, List<TimelineEvent>> getData() {
        return Collections.unmodifiableMap(list);
    }

    public void add(EventDto e, Object metadata) {
        TimelineEvent event = new TimelineEvent(
                e.getTimestamp(),
                e.getUser(),
                EventsUtil.event2Classname(e),
                EventsUtil.event2name(e),
                null,
                metadata
        );
        add(event);
    }
}

