package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;


/**
 * Created by Seky on 8. 8. 2014.
 */
public abstract class ProcessAsGroup extends ProcessIterator<EventDto> {
    private IDividing divide;
    private Group currentGroup;

    protected ProcessAsGroup() {
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

    protected abstract void foundEndOfGroup(Group group);

    @Override
    protected void finished() {
        if (!(currentGroup == null || currentGroup.isEmpty())) {
            foundEndOfGroup(currentGroup);
        }
    }
}
