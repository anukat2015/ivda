package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.server.Catalog;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Pomocna trieda len pre vyvoj! Na hladanie pouzitych procesov a vypisanie ich nazvu.
 */
@NotThreadSafe
public final class PrintProcesses extends ProcessEventsToDataTable {
    private final Set<String> processes = new HashSet<>(1000);

    public PrintProcesses() {
        checkFiles();
    }


    public void checkLists(Catalog.Processes a, Catalog.Processes b) {
        Set<String> same = a.getList().getSameValues(b.getList());
        if (same.isEmpty()) {
            return;
        }
        LOGGER.warn(a.getList().getFileName() + " == " + b.getList().getFileName() + " => ");
        for (String name : same) {
            LOGGER.warn(name);
        }
    }

    public void checkFiles() {
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.COMMUNICATION);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.NODEVELOPER, Catalog.Processes.TYPICAL);
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof ProcessesChangedSinceCheckEventDto)) return;
        ProcessesChangedSinceCheckEventDto cevent = (ProcessesChangedSinceCheckEventDto) event;
        addAll(cevent.getStartedProcesses());
        addAll(cevent.getKilledProcesses());
    }

    private void addAll(List<ProcessDto> list) {
        for (ProcessDto p : list) {
            processes.add(p.getName());
        }
    }

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