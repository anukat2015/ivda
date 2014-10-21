package sk.stuba.fiit.perconik.ivda.server.grouping;

import com.google.visualization.datasource.base.TypeMismatchException;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents2TimelineEvents;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;


/**
 * Created by Seky on 8. 8. 2014.
 */
public class ProcessAsGroup extends ProcessEvents2TimelineEvents {
    private final IDividing divide;
    private IGrouping group;

    public ProcessAsGroup() {
        divide = new DivideByOnline();
    }

    /**
     * Zakladnym principom je vytvoreny interval od prveho az po posledny event. Nasledne na zaklade casu
     * alebo roznych typov sa interval pretrhne.
     *
     * @param event
     * @throws TypeMismatchException
     */
    @Override
    protected void proccessItem(EventDto event) {
        if (divide.canIgnore(event)) {
            return;
        }

        // Ak si prvy uloz sa a pokracuj dalej
        if (group == null) {
            group = createNewGroup(event);
            group.add2Group(event);  // musi sa nastavit skor ako v checkGroup
            return; // moze pokracovat dalej, checkGroup neovplinvi ked prvya posledny je ten isty ale nemusi :)
        }

        if (divide.canDivide(group, event)) {
            foundEndOfGroup(group);
            group = createNewGroup(event);
        }
        group.add2Group(event);
    }

    protected IGrouping createNewGroup(EventDto event) {
        return new BoundedGroup(event);
    }

    protected void foundEndOfGroup(IGrouping group) {
        // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
        EventDto first = group.getFirstEvent();
        EventDto last = group.getLastEvent();
        TimelineEvent event = new TimelineEvent(
                first.getTimestamp(),
                first.getUser(),
                EventsUtil.event2Classname(first),
                EventsUtil.event2name(first),
                last.getTimestamp(),
                group.size());

        add(event);
    }

    @Override
    public void finished() {
        if (group == null || group.isEmpty()) {
            return; // ked ziadny prvok nebol v odpovedi
        }
        foundEndOfGroup(group);
    }
}
