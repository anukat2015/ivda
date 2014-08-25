package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Skupina reprezentovana akzdym jednym prvkom.
 */
public class FullGroup implements IGrouping, Serializable {
    private static final long serialVersionUID = 8640052504548837517L;
    private final List<EventDto> list;

    public FullGroup() {
        list = new ArrayList<>();
    }

    @Override
    public void add2Group(EventDto event) {
        list.add(event);
    }

    @Override
    public EventDto getLastEvent() {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        return list.get(size - 1);
    }

    @Override
    public EventDto getFirstEvent() {
        return list.get(0);
    }

    @Override
    public int size() {
        return list.size();
    }
}