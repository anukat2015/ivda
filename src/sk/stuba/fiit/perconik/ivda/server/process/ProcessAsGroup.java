package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.server.processes.BlackListedProcesses;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.web.WebEventDto;

import java.util.concurrent.TimeUnit;


/**
 * Created by Seky on 8. 8. 2014.
 */
public class ProcessAsGroup extends ProcessEventsToDataTable {
    /**
     * tzv. raz za minutu sa posle event, vtedy vieme urcite ze je aktivny
     */
    private static final long ACTIVITY_MIN_INTERVAL = TimeUnit.MINUTES.toMillis(1);

    /**
     * tzv. raz za 5 min s amusi poslat udaj o procesoch, vtedy sa da chapat, ze je na pocitaci ale eventy sa nezachytavaju
     * napriklad pzoera film
     */
    private static final int PROCESS_MIN_INTERVAL = 1000 * 60 * 5;
    private final BlackListedProcesses blacklist;
    private EventDto firstEvent = null;  // potrebne a urcenie zaciatocnej pozicie
    private EventDto lastEvent = null;  // potrebne pre meranie podla casu
    private Integer inGroup = 0;

    public ProcessAsGroup(EventsRequest request) {
        super(request);
        blacklist = new BlackListedProcesses();
        if (!request.getAscending()) {
            throw new IllegalArgumentException("Ascening should be true");
        }
    }

    /**
     * Zakladnym principom je vytvoreny interval od prveho az po posledny event. Nasledne na zaklade casu
     * alebo roznych typov sa interval pretrhne.
     *
     * @param event
     * @throws TypeMismatchException
     */
    @Override
    protected void proccessItem(EventDto event) {
        // Ignorujeme malo podstatne entity  ... startovanie monitorovania neznamena nic
        if (event instanceof MonitoringStartedEventDto) {
            return;
        }


        // Dalsie entity znamenaju aktivitu, ProcessesChangedSinceCheckEventDto sa miesa spolu s ostatynmi aktivitamy preto ich ignorujeme
        if (event instanceof ProcessesChangedSinceCheckEventDto) {
            if (!checkProcesses((ProcessesChangedSinceCheckEventDto) event)) {
                // Nejde o zaujimavy proces, pravdepodobne nic nerobil
                return;
            }
        }

        // Ak si prvy uloz sa a pokracuj dalej
        if (firstEvent == null) {
            createNewGroup(event);
            add2Group(event);  // musi sa nastavit skor ako v checkGroup
            return; // moze pokracovat dalej, checkGroup neovplinvi ked prvya posledny je ten isty ale nemussi :)
        }

        if (divideByTime(event) || divideByType(event)) {
            foundEndOfGroup();
            createNewGroup(event);
        }
        add2Group(event);
    }

    protected void add2Group(EventDto event) {
        lastEvent = event;
        inGroup++;
    }

    /**
     * Vyvojar mozno zapol inu aplikaciu ako WEB alebo IDE a tym padom stale pracoval.
     * Skontroluj to.
     *
     * @param actual
     * @return
     */
    protected boolean checkProcesses(ProcessesChangedSinceCheckEventDto actual) {
        return blacklist.checkAtLeastOneAllowed(actual.getStartedProcesses());
    }

    /**
     * Ked interval medzi eventami je priliz velky, rozdel interval.
     *
     * @param actual
     * @return
     * @throws TypeMismatchException
     */
    protected boolean divideByTime(EventDto actual) {
        long actualTimestamp = actual.getTimestamp().getTimeInMillis();
        long lastTimestamp = getLastEvent().getTimestamp().getTimeInMillis();
        long diff = actualTimestamp - lastTimestamp;
        return (diff > ACTIVITY_MIN_INTERVAL);  // Je to velky casovy rozdiel
    }

    public EventDto getLastEvent() {
        return lastEvent;
    }

    public EventDto getFirstEvent() {
        return firstEvent;
    }

    /**
     * Rozdel interval ked prvky su odlisneho typu
     *
     * @param actual
     * @return
     * @throws TypeMismatchException
     */
    protected boolean divideByType(EventDto actual) {
        if (getLastEvent() instanceof WebEventDto && actual instanceof WebEventDto) {
            return false; // su rovnake
        } else if (getLastEvent() instanceof IdeEventDto && actual instanceof IdeEventDto) {
            return false; // su rovnake
        }
        // nie su rovnake alebo pojde o novy typ
        return true;
    }

    protected void createNewGroup(EventDto actual) {
        inGroup = 0;
        firstEvent = actual;
    }

    protected void foundEndOfGroup() {
        // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
        GregorianCalendar start = getFirstEvent().getTimestamp();
        GregorianCalendar end = getLastEvent().getTimestamp();
        dataTable.add(getFirstEvent().getUser(), start, end, EventsUtil.event2Classname(getFirstEvent()), EventsUtil.event2name(getFirstEvent()), inGroup.toString());
    }

    @Override
    protected void finished() {
        if (getFirstEvent() == null) {
            return; // ked ziadny prvok nebol v odpovedi
        }
        foundEndOfGroup();
    }
}
