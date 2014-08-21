package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.uaca.dto.EventDto;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * IGroup reprezentuje spravanie skupiny.
 */
public interface IGrouping {
    void add2Group(EventDto event);

    EventDto getLastEvent();

    EventDto getFirstEvent();

    default boolean isEmpty() {
        return getFirstEvent() == null;
    }

    int size();
}
