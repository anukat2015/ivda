package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Interaface urcuje sposob spravania ako sa skupiny maju rozdelovat.
 */
public interface IDividing {
    boolean canDivide(Group group, EventDto event);

    boolean canIgnore(EventDto event);
}
