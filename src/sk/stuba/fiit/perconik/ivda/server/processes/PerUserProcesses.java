package sk.stuba.fiit.perconik.ivda.server.processes;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Seky on 22. 10. 2014.
 * <p/>
 * Zoskupene procesy pre jedneho pouzivatela.
 */
public class PerUserProcesses implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(PerUserProcesses.class.getName());
    private static final long serialVersionUID = -2656574415238396199L;

    private final List<Process> finished;
    private final Map<Integer, Process> unfinished;

    public PerUserProcesses() {
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
}
