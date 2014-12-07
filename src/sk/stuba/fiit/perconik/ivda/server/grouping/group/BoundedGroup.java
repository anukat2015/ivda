package sk.stuba.fiit.perconik.ivda.server.grouping.group;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupina reprezentovana len 2 hranicnymi bodmi.
 * V skupine moze byt aj jeden bod.
 */
@NotThreadSafe
public class BoundedGroup extends Group implements Serializable {
    private static final long serialVersionUID = 2522657623315850131L;

    private EventDto firstEvent;  // potrebne a urcenie zaciatocnej pozicie
    private EventDto lastEvent;  // potrebne pre meranie podla casu
    private int inGroup;

    public BoundedGroup() {  // alias createNewGroup
        inGroup = 0;
    }

    @Override
    public void push(EventDto event) {
        if(firstEvent == null) {
            firstEvent = event;
        }
        lastEvent = event;
        inGroup++;
    }

    @Override
    public EventDto getLastEvent() {
        return lastEvent;
    }

    @Override
    public EventDto getFirstEvent() {
        return firstEvent;
    }

    @Override
    public int countEvents() {
        return inGroup;
    }
}
