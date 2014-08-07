package sk.stuba.fiit.perconik.ivda.server;

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.Configuration;
import sk.stuba.fiit.perconik.ivda.DateUtils;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessesChangedSinceCheckEventDto;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Seky on 7. 8. 2014.
 */
public abstract class FindFinishedProcess {
    private static final Logger logger = Logger.getLogger(FindFinishedProcess.class.getName());
    private static final String fileName = "processBlackList.txt";

    protected Map<Integer, FullProcess> startedApps;
    protected Set<String> appBlackList;

    public FindFinishedProcess() {
        File file = new File(Configuration.CONFIG_DIR, fileName);
        try {
            appBlackList = new HashSet<>(FileUtils.readLines(file));
        } catch (IOException e) {
            logger.error("Error reading from " + fileName, e);
        }
        startedApps = new HashMap<>();
    }

    protected void check(ProcessesChangedSinceCheckEventDto event) {
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().toGregorianCalendar().getTime());

        for (ProcessDto started : event.getStartedProcesses()) {
            if (appBlackList.contains(started.getName())) {
                continue;
            }
            FullProcess item = new FullProcess();
            item.start = timestamp;
            item.process = started;
            FullProcess saved = startedApps.put(started.getPid(), item);
            if (saved != null) {
                logger.info("Process s takym PID uz existuje...");
            }
        }

        for (ProcessDto killed : event.getKilledProcesses()) {
            FullProcess saved = startedApps.get(killed.getPid());
            if (saved != null) {
                startedApps.remove(killed.getPid());
                finded(saved);
            }
        }
    }

    protected abstract void finded(FullProcess process);

    public void flushUnfinished() {
        Set<Map.Entry<Integer, FullProcess>> set = startedApps.entrySet();
        for (Map.Entry<Integer, FullProcess> entry : set) {
            logger.info("Nema hranicu " + entry.getValue().process.getName()
                            + " " + DateUtils.toString(entry.getValue().start)
                            + " " + Integer.toString(entry.getValue().process.getPid())
            );
        }
    }

    protected class FullProcess {
        public GregorianCalendar start;
        public GregorianCalendar end;
        public ProcessDto process;
    }
}
