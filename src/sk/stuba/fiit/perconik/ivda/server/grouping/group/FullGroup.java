package sk.stuba.fiit.perconik.ivda.server.grouping.group;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupina reprezentovana kazdym jednym prvkom.
 */
@NotThreadSafe
public class FullGroup extends Group implements Serializable {
    private static final long serialVersionUID = 8640052504548837517L;
    private final List<EventDto> list;

    public FullGroup() {
        list = new ArrayList<>();
    }

    @Override
    public void push(EventDto event) {
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
    public int countEvents() {
        return list.size();
    }
}
