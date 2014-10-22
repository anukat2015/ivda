package sk.stuba.fiit.perconik.ivda.server.processes;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Seky on 22. 10. 2014.
 */
public class PerUserInfo {
    private static final Logger LOGGER = Logger.getLogger(PerUserInfo.class.getName());

    private final List<Process> finished;
    private final Map<Integer, Process> unfinished;

    public PerUserInfo() {
        finished = new ArrayList<>(256);
        unfinished = new HashMap<>(256);
    }

    protected void printProcesses(Collection<Process> processes) {
        for (Process p : processes) {
            LOGGER.info(p);
        }
    }

    public void printFinished() {
        printProcesses(finished);
    }

    public void printUnfinished() {
        printProcesses(unfinished.values());
    }

    public List<Process> getFinished() {
        return finished;
    }

    public Map<Integer, Process> getUnfinished() {
        return unfinished;
    }

    public void clear() {
        unfinished.clear();
    }
}
