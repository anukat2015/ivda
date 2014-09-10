package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * IGroup reprezentuje spravanie skupiny.
 */
public interface IGrouping {
    void add2Group(EventDto event);

    EventDto getLastEvent();

    EventDto getFirstEvent();

    boolean isEmpty();

    int size();
}
