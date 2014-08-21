package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import java.io.Serializable;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Skupina reprezentovana len 2 hranicnymi bodmi.
 */
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
    public int size() {
        return inGroup;
    }
}
