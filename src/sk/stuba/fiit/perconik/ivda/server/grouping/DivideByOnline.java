package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.MonitoringStartedEventDto;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Rozdeln skupiny podla toho ci je pouzivatel online.
 */
@ThreadSafe
public class DivideByOnline implements IDividing {
    /**
     * tzv. raz za 6 min sa musi poslat udaj o procesoch, vtedy sa da chapat, ze je na pocitaci ale eventy sa nezachytavaju
     * napriklad pzoera film
     */
    private static final long ONLINE_MIN_INTERVAL = TimeUnit.MINUTES.toMillis(6L);

    @Override
    public boolean canDivide(IGrouping group, EventDto actual) {
        return (DateUtils.diff(actual.getTimestamp(), group.getLastEvent().getTimestamp()) > ONLINE_MIN_INTERVAL);
    }

    @Override
    public boolean canIgnore(EventDto event) {
        // Ignorujeme malo podstatne entity  ... startovanie monitorovania neznamena nic
        if (event instanceof MonitoringStartedEventDto) {
            return true;
        }

        return false;
    }
}
