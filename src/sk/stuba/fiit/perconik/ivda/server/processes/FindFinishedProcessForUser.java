package sk.stuba.fiit.perconik.ivda.server.processes;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.server.Catalog;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Seky on 7. 8. 2014.
 * <p/>
 * Trieda sa pozera na vytvorene a ukoncene procesy.
 * Pomocou PID procesu hlada zaciatok a ukoncenie procesu.
 * Nasledne danemu procesu priradi aproximovane casi zaciatku a ukoncenia.
 * Mnohe procesy nas nezaujimaju na to sluzi black lis procesov
 */
@NotThreadSafe
public abstract class FindFinishedProcessForUser {
    private static final Logger LOGGER = Logger.getLogger(FindFinishedProcessForUser.class.getName());

    private final Map<Integer, FinishedProcess> startedApps;
    private final Catalog appBlackList;

    protected FindFinishedProcessForUser() {
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
                LOGGER.warn("Process s takym PID uz existuje... " + saved);
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

    /**
     * Vycisti doteraz zachytene neukoncene procesy
     */
    public void clear() {
        startedApps.clear();
    }

    public static final class FinishedProcess {
        public Date start;
        public Date end;
        public ProcessDto process;

        @Override
        public String toString() {
            return process.getName()
                    + ' ' + Integer.toString(process.getPid())
                    + ' ' + DateUtils.toString(start);
        }
    }
}
