package sk.stuba.fiit.perconik.ivda.server.grouping.group;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupina eventov.
 */
public abstract class Group {
    public abstract void add2Group(EventDto event);

    public abstract EventDto getLastEvent();

    public abstract EventDto getFirstEvent();

    public abstract boolean isEmpty();

    public abstract int size();

    public long getTimeDiff() {
        if (isEmpty()) {
            return 0;
        }
        return DateUtils.diff(getFirstEvent().getTimestamp(), getLastEvent().getTimestamp());
    }
}
