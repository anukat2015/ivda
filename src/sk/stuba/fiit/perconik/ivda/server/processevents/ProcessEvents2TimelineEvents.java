package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;

import java.util.*;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Metoda spracovania udalosti, ktora je rozsirena o moznost ukladat zaujimave udalosti.
 */
public abstract class ProcessEvents2TimelineEvents extends ProcessEvents {
    private final List<TimelineEvent> list;

    protected ProcessEvents2TimelineEvents() {
        list = new ArrayList<>(128);
    }

    public void add(TimelineEvent event) {
        // Black out developer name
        String group = Developers.getInstance().blackoutName(event.getGroup());
        event.setGroup(group);
        list.add(event);
    }

    public List<TimelineEvent> getData() {
        return Collections.unmodifiableList(list);
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

