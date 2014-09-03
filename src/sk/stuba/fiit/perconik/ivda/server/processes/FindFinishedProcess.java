package sk.stuba.fiit.perconik.ivda.server.processes;

import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.Catalog;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.uaca.dto.ProcessDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Seky on 7. 8. 2014.
 * <p>
 * Trieda sa pozera na vytvorene a ukoncene procesy.
 * Pomocou PID procesu hlada zaciatok a ukoncenie procesu.
 * Nasledne danemu procesu priradi aproximovane casi zaciatku a ukoncenia.
 * Mnohe procesy nas nezaujimaju na to sluzi black lis procesov
 */
@NotThreadSafe
public abstract class FindFinishedProcess {
    private static final Logger LOGGER = Logger.getLogger(FindFinishedProcess.class.getName());

    private final Map<Integer, FinishedProcess> startedApps;
    private final Catalog appBlackList;

    protected FindFinishedProcess() {
        startedApps = new HashMap<>(200);
        appBlackList = Catalog.Processes.BANNED.getList();
    }

    /**
     * Skontroluj zoznam procesov.
     *
     * @param event
     */
    public final void handle(ProcessesChangedSinceCheckEventDto event) {
        handleStarted(event);
        handleKilled(event);
    }

    private void handleStarted(ProcessesChangedSinceCheckEventDto event) {
        for (ProcessDto started : event.getStartedProcesses()) {
            if (appBlackList.exist(started)) {
                continue;
            }

            FinishedProcess item = new FinishedProcess();
            item.start = event.getTimestamp();
            item.process = started;
            FinishedProcess saved = startedApps.put(started.getPid(), item);
            if (saved != null) {
                LOGGER.info("Process s takym PID uz existuje... " + saved);
            }
        }
    }

    private void handleKilled(ProcessesChangedSinceCheckEventDto event) {
        for (ProcessDto killed : event.getKilledProcesses()) {
            FinishedProcess saved = startedApps.get(killed.getPid());
            if (saved != null) {
                startedApps.remove(killed.getPid());
                saved.end = event.getTimestamp();
                found(saved);
            }
        }
    }

    /**
     * Nasiel sa zaciatok a koniec procesu.
     *
     * @param process
     */
    protected abstract void found(FinishedProcess process);

    /**
     * Vypis neukoncene procesy.
     */
    public final void flushUnfinished() {
        Set<Map.Entry<Integer, FinishedProcess>> set = startedApps.entrySet();
        for (Map.Entry<Integer, FinishedProcess> entry : set) {
            LOGGER.info("Nema hranicu " + entry.getValue());
        }
    }

    protected static final class FinishedProcess {
        public GregorianCalendar start;
        public GregorianCalendar end;
        public ProcessDto process;

        @Override
        public String toString() {
            return process.getName()
                    + ' ' + Integer.toString(process.getPid())
                    + ' ' + DateUtils.toString(start);
        }
    }
}
