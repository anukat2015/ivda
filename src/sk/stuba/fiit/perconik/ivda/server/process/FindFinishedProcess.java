package sk.stuba.fiit.perconik.ivda.server.process;

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

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
public abstract class FindFinishedProcess {
    private static final Logger logger = Logger.getLogger(FindFinishedProcess.class.getName());

    protected Map<Integer, FinishedProcess> startedApps;
    protected BlackListedProcesses appBlackList;

    public FindFinishedProcess() {
        startedApps = new HashMap<>();
        appBlackList = new BlackListedProcesses();
    }

    /**
     * Skontroluj zoznam procesov.
     *
     * @param event
     */
    protected void check(ProcessesChangedSinceCheckEventDto event) {
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().getTime());

        for (ProcessDto started : event.getStartedProcesses()) {
            if (appBlackList.contains(started)) {
                continue;
            }
            FinishedProcess item = new FinishedProcess();
            item.start = timestamp;
            item.process = started;
            FinishedProcess saved = startedApps.put(started.getPid(), item);
            if (saved != null) {
                logger.info("Process s takym PID uz existuje... " + saved);
            }
        }

        for (ProcessDto killed : event.getKilledProcesses()) {
            FinishedProcess saved = startedApps.get(killed.getPid());
            if (saved != null) {
                startedApps.remove(killed.getPid());
                saved.end = timestamp;
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
    public void flushUnfinished() {
        Set<Map.Entry<Integer, FinishedProcess>> set = startedApps.entrySet();
        for (Map.Entry<Integer, FinishedProcess> entry : set) {
            logger.info("Nema hranicu " + entry.getValue());
        }
    }

    protected class FinishedProcess {
        public GregorianCalendar start;
        public GregorianCalendar end;
        public ProcessDto process;

        @Override
        public String toString() {
            return process.getName()
                    + " " + Integer.toString(process.getPid())
                    + " " + DateUtils.toString(start);
        }
    }
}
