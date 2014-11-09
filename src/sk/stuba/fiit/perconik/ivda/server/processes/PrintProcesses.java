package sk.stuba.fiit.perconik.ivda.server.processes;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.util.Catalog;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Metoda spracovania procesov, ktora vypise zoznam vsetkych procesov, ktore sa nenachadzaju ani v ejdnom white lsiste.
 * Metoda je urcena ako pomocka pri analyze dat.
 */
@NotThreadSafe
public final class PrintProcesses extends ProcessProcesses implements Serializable {
    private static final long serialVersionUID = 2949364093850819480L;
    private static final Logger LOGGER = Logger.getLogger(PrintProcesses.class.getName());
    private final Set<String> processes = new HashSet<>(1000);

    @Override
    protected void handleStarted(ProcessesChangedSinceCheckEventDto event, ProcessDto p) {
        processes.add(p.getName());
    }

    @Override
    protected void handleKilled(ProcessesChangedSinceCheckEventDto event, ProcessDto p) {
        processes.add(p.getName());
    }

    @Override
    public void finished() {
        LOGGER.info("New processes:");
        for (String name : processes) {
            if (!Catalog.Processes.BANNED.getList().exist(name) &&
                    !Catalog.Processes.NODEVELOPER.getList().exist(name) &&
                    !Catalog.Processes.COMMUNICATION.getList().exist(name) &&
                    !Catalog.Processes.TYPICAL.getList().exist(name)) {
                LOGGER.warn(name);
            }
        }
    }
}
