package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.server.Catalog;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Pomocna trieda len pre vyvoj! Na hladanie pouzitych procesov a vypisanie ich nazvu.
 */
@NotThreadSafe
public final class PrintProcesses extends ProcessProcesses implements Serializable {
    private static final long serialVersionUID = 2949364093850819480L;
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
        System.out.println("New processes:");
        for (String name : processes) {
            if (!Catalog.Processes.BANNED.getList().contains(name) &&
                    !Catalog.Processes.NODEVELOPER.getList().contains(name) &&
                    !Catalog.Processes.COMMUNICATION.getList().contains(name) &&
                    !Catalog.Processes.TYPICAL.getList().contains(name)) {
                LOGGER.warn(name);
            }
        }
    }
}
