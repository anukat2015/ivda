package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEventsOut;
import sk.stuba.fiit.perconik.ivda.server.servlets.IvdaEvent;

import java.io.OutputStream;


/**
 * Created by Seky on 8. 8. 2014.
 */
public class ProcessAsGroup extends ProcessEventsOut {
    private final IDividing divide;
    private Group currentGroup;

    protected ProcessAsGroup(OutputStream out) {
        super(out);
        divide = new DivideByTimeAndType();
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
        if (currentGroup == null) {
            currentGroup = createNewGroup(event);
            currentGroup.add2Group(event);  // musi sa nastavit skor ako v checkGroup
            return; // moze pokracovat dalej, checkGroup neovplinvi ked prvya posledny je ten isty ale nemusi :)
        }

        if (divide.canDivide(currentGroup, event)) {
            foundEndOfGroup(currentGroup);
            currentGroup = createNewGroup(event);
        }
        currentGroup.add2Group(event);
    }

    protected Group createNewGroup(EventDto event) {
        return new BoundedGroup(event);
    }

    protected void foundEndOfGroup(Group group) {
        // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
        EventDto first = group.getFirstEvent();
        EventDto last = group.getLastEvent();

        // Store event
        IvdaEvent event = new IvdaEvent();
        event.setStart(first.getTimestamp());
        event.setEnd(last.getTimestamp());
        event.setGroup(EventsUtil.event2name(first));
        event.setY(group.size());
        add(event);
    }

    @Override
    public void finished() {
        if (!(currentGroup == null || currentGroup.isEmpty())) {
            foundEndOfGroup(currentGroup);
        }
        super.finished();
    }
}
