package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.uaca.dto.EventDto;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Interaface urcuje sposob spravania ako sa skupiny maju rozdelovat.
 */
public interface IDividing {
    boolean canDivide(IGrouping group, EventDto event);

    boolean canIgnore(EventDto event);
}
