package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupina reprezentovana len 2 hranicnymi bodmi.
 */
@NotThreadSafe
public class BoundedGroup implements IGrouping, Serializable {
    private static final long serialVersionUID = 2522657623315850131L;

    private final EventDto firstEvent;  // potrebne a urcenie zaciatocnej pozicie
    private EventDto lastEvent;  // potrebne pre meranie podla casu
    private int inGroup;

    public BoundedGroup(EventDto actual) {  // alias createNewGroup
        inGroup = 0;
        firstEvent = actual;
    }

    @Override
    public void add2Group(EventDto event) {
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
    public boolean isEmpty() {
        return getFirstEvent() == null;
    }

    @Override
    public int size() {
        return inGroup;
    }


}
