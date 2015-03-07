package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.BashCommandEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Skupiny rozdelujeme na zaklade casu alebo typu.
 */
@ThreadSafe
public class DividingByEnviroment implements IDividing {

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
    public boolean canPush(Group currentGroup, EventDto actual) {
        return !divideByTime(currentGroup, actual);
    }

    /**
     * Ma sa skupina rozdelit?
     * @param group  Aktualna skupina.
     * @param actual Pricadzajuci event.
     * @return
     */
    @Override
    public boolean canDivide(Group group, EventDto actual) {
        return divideByEnviroment(group, actual);
    }

    /**
     * Tzv. raz za minutu sa posle event, vtedy vieme urcite ze je aktivny
     */
    private static final long ACTIVITY_MIN_INTERVAL = TimeUnit.MINUTES.toMillis(Configuration.getInstance().getActivityMinIntervalTh());

    /**
     * Ked interval medzi eventami je priliz velky, rozdel interval.
     *
     * @param actual
     * @return
     * @throws com.google.visualization.datasource.base.TypeMismatchException
     */
    protected static boolean divideByTime(Group group, EventDto actual) {
        return (DateUtils.diff(group.getLastEvent().getTimestamp(), actual.getTimestamp()) > ACTIVITY_MIN_INTERVAL);  // Je to velky casovy rozdiel
    }

    /**
     * Rozdel interval ked prvky su odlisneho typu.
     * Tzv tuto delime podla prostredia.
     *
     * @param actual
     * @return
     */
    protected static boolean divideByEnviroment(Group group, EventDto actual) {
        if (group.getFirstEvent() instanceof WebEventDto && actual instanceof WebEventDto) {
            return false; // su rovnake
        } else if (group.getFirstEvent() instanceof IdeEventDto && actual instanceof IdeEventDto) {
            return false; // su rovnake
        } else if (group.getFirstEvent() instanceof BashCommandEventDto && actual instanceof BashCommandEventDto) {
            return false; // su rovnake
        }

        // nie su rovnake alebo pojde o novy typ
        return true;
    }
}
