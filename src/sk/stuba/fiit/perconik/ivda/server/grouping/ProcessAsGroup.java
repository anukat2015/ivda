package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.BoundedGroup;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;


/**
 * Created by Seky on 8. 8. 2014.
 * Trieda predstavuje zakladne budovanie skupiny  z udalosti.
 * Udalost moze byt ignorovana.
 * Udalost moze byt byt a nemusi byt pridana do skupiny. Napriklad na zaklade casu od poslednej udalosti.
 * Na zaklade noveho typu udalosti, moze byt doterajsia skupina uzatvorena a vznikne nova skupina.
 */
public abstract class ProcessAsGroup extends ProcessIterator<EventDto> {
    private IDividing divide;
    private Group currentGroup;

    protected ProcessAsGroup(IDividing divide) {
        this.divide = divide;
    }

    protected ProcessAsGroup() {
        this(new DividingByEnviroment());
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
            currentGroup.push(event);
            return;
        }

        boolean canPush = divide.canPush(currentGroup, event);
        boolean canDivide = false;
        if (canPush) {
            canDivide = divide.canDivide(currentGroup, event);
            currentGroup.push(event);
        } else {
            canDivide = true;
        }

        if (canDivide) {
            foundEndOfGroup(currentGroup);
            currentGroup = createNewGroup(event);
            currentGroup.push(event);
        }
    }

    protected Group createNewGroup(EventDto event) {
        return new BoundedGroup();
    }

    protected abstract void foundEndOfGroup(Group group);

    @Override
    protected void finished() {
        if (currentGroup != null) {
            foundEndOfGroup(currentGroup);
        }
    }
}
