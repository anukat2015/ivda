package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.BashCommandEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupiny rozdelujeme na zaklade casu alebo typu.
 */
@ThreadSafe
public class BaseDividing implements IDividing {

    @Override
    public boolean canIgnore(EventDto event) {
        // Ignorujeme malo podstatne entity  ... startovanie monitorovania neznamena nic
        if (event instanceof MonitoringStartedEventDto) {
            return true;
        }

        // Dalsie entity znamenaju aktivitu, ProcessesChangedSinceCheckEventDto sa miesa spolu s ostatynmi aktivitamy preto ich ignorujeme
        if (event instanceof ProcessesChangedSinceCheckEventDto) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canDivide(Group group, EventDto actual) {
        return divideByTime(group, actual) || divideByType(group, actual);
    }

    /**
     * Tzv. raz za minutu sa posle event, vtedy vieme urcite ze je aktivny
     */
    private static final long ACTIVITY_MIN_INTERVAL = TimeUnit.MINUTES.toMillis(6L);

    /**
     * Ked interval medzi eventami je priliz velky, rozdel interval.
     *
     * @param actual
     * @return
     * @throws com.google.visualization.datasource.base.TypeMismatchException
     */
    public static boolean divideByTime(Group group, EventDto actual) {
        return (DateUtils.diff(group.getLastEvent().getTimestamp(), actual.getTimestamp()) > ACTIVITY_MIN_INTERVAL);  // Je to velky casovy rozdiel
    }

    /**
     * Rozdel interval ked prvky su odlisneho typu
     *
     * @param actual
     * @return
     * @throws com.google.visualization.datasource.base.TypeMismatchException
     */
    public static boolean divideByType(Group group, EventDto actual) {
        if (group.getLastEvent() instanceof WebEventDto && actual instanceof WebEventDto) {
            return false; // su rovnake
        } else if (group.getLastEvent() instanceof IdeEventDto && actual instanceof IdeEventDto) {
            return false; // su rovnake
        } else if (group.getLastEvent() instanceof BashCommandEventDto && actual instanceof BashCommandEventDto) {
            return false; // su rovnake
        }

        // nie su rovnake alebo pojde o novy typ
        return true;
    }

    public static boolean divideForWebTabSpendTime(Group group, EventDto actual) {
        EventDto first = group.getFirstEvent();
        if (!(first instanceof WebEventDto)) {
            return false; // nejde o Web, ignorujeme, rozdeli to nieco dalsie
        }
        if (!(actual instanceof WebEventDto)) {
            return true; // druhy prvok je iny typ
        }
        String url1 = ((WebEventDto) first).getDomain();
        String url2 = ((WebEventDto) actual).getDomain();
        if (url1 == null || url2 == null) {
            return true;
        }
        // url1 ponechaj take ake je, ked je ine ako url2 rozdeli to samo, co je pravdepodobne, inak mohol byt aj na rovnakej neznamen stranke
        return group.size() > 1 && !url1.equals(url2); // tzv prida event na zaciatok a na konci bude druhy event
    }
}
