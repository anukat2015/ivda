package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;

/**
 * Created by Seky on 4. 12. 2014.
 */
public class PerWebDividing extends BaseDividing {
    @Override
    public boolean canDivide(Group group, EventDto actual) {    // Zapneme per domain filter
        return divideByTime(group, actual) || divideForWebTabSpendTime(group, actual) || divideByType(group, actual);
    }
}
