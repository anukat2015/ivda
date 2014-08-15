package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.server.BlackListedProcesses;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.web.WebEventDto;

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
    private EventDto firstEvent = null;  // potrebne a urcenie zaciatocnej pozicie
    private EventDto lastEvent = null;  // potrebne pre meranie podla casu
    private Integer inGroup = 0;

    private BlackListedProcesses blacklist;

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
    protected void proccessItem(EventDto event) throws TypeMismatchException {
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
            firstEvent = event;
            lastEvent = event;
            return;
        }

        inGroup++;
        checkGroup(event);
        lastEvent = event;
    }

    /**
     * Vyvojar mozno zapol inu aplikaciu ako WEB alebo IDE a tym padom stale pracoval.
     * Skontroluj to.
     *
     * @param actual
     * @return
     */
    protected boolean checkProcesses(ProcessesChangedSinceCheckEventDto actual) {
        return blacklist.atLeastOneSpecial(actual.getStartedProcesses());
    }

    /**
     * Ked interval medzi eventami jepriliz velky, rozdel interval.
     *
     * @param actual
     * @return
     * @throws TypeMismatchException
     */
    protected boolean parseByTime(EventDto actual) throws TypeMismatchException {
        long actualTimestamp = actual.getTimestamp().getTimeInMillis();
        long lastTimestamp = lastEvent.getTimestamp().getTimeInMillis();
        long diff = actualTimestamp - lastTimestamp;
        if (diff > ACTIVITY_MIN_INTERVAL) {
            // Je to velky casovy rozdiel
            foundGroupEnd();
            firstEvent = actual;
            return true;
        }
        return false;
    }

    protected void checkGroup(EventDto actual) throws TypeMismatchException {
        if (parseByTime(actual)) return;
        /*
        if (lastEvent instanceof WebEventDto && firstEvent instanceof WebEventDto) {
            return; // su rovnake
        } else if (lastEvent instanceof IdeEventDto && firstEvent instanceof IdeEventDto) {
            return; // su rovnake
        } else {   // nie su rovnake alebo pojde o novy typ
            foundGroupEnd();
            firstEvent = lastEvent;
            return;
        }  */
    }

    protected void foundGroupEnd() throws TypeMismatchException {
        // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
        GregorianCalendar start = firstEvent.getTimestamp();
        GregorianCalendar end = lastEvent.getTimestamp();
        MyDataTable.ClassName type;
        String content;
        if (firstEvent instanceof WebEventDto) {
            type = MyDataTable.ClassName.AVAILABLE;
            content = "Web";
        } else if (firstEvent instanceof IdeEventDto) {
            type = MyDataTable.ClassName.MAYBE;
            content = "Ide";
        } else if (firstEvent instanceof ProcessesChangedSinceCheckEventDto) {
            type = MyDataTable.ClassName.AVAILABLE;
            content = "Iny proces";
        } else {
            logger.warn("Neznamy typ entity prisiel az sem.");
            type = MyDataTable.ClassName.UNAVAILABLE; // tzv nezanmy typ entity prisiel
            content = "Unknown";
        }

        dataTable.add(firstEvent.getUser(), start, end, type, content, inGroup.toString());
        inGroup = 0;
    }

    protected void finished() {
        if (firstEvent == null) {
            return; // ked ziadny prvok nebl v odpovedi
        }
        try {
            foundGroupEnd();
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        }
    }
}
