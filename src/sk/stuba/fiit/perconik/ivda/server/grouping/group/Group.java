package sk.stuba.fiit.perconik.ivda.server.grouping.group;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupina eventov.
 */
public abstract class Group {
    public abstract void push(EventDto event);

    public abstract EventDto getLastEvent();

    public abstract EventDto getFirstEvent();

    public boolean isInterval() {
        return countEvents() > 1;
    }

    public abstract int countEvents();

    public long getTimeDiff() {
        if (!isInterval()) {
            return 0;
        }
        return DateUtils.diff(getFirstEvent().getTimestamp(), getLastEvent().getTimestamp());
    }
}
