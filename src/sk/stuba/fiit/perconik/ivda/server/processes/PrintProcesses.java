package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
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

    public PrintProcesses(EventsRequest request) {
        super(request);
        checkFiles();
    }


    public void checkLists(ListOfProcesses.Type a, ListOfProcesses.Type b) {
        Set<String> same = a.getList().getSameProcesses(b.getList());
        if (same.isEmpty()) {
            return;
        }
        LOGGER.warn(a.getList().getFileName() + " == " + b.getList().getFileName() + " => ");
        for (String name : same) {
            LOGGER.warn(name);
        }
    }

    public void checkFiles() {
        checkLists(ListOfProcesses.Type.BLACK_LIST, ListOfProcesses.Type.COMMUNICATION);
        checkLists(ListOfProcesses.Type.BLACK_LIST, ListOfProcesses.Type.NODEVELOPER);
        checkLists(ListOfProcesses.Type.BLACK_LIST, ListOfProcesses.Type.TYPICAL);
        checkLists(ListOfProcesses.Type.COMMUNICATION, ListOfProcesses.Type.NODEVELOPER);
        checkLists(ListOfProcesses.Type.COMMUNICATION, ListOfProcesses.Type.TYPICAL);
        checkLists(ListOfProcesses.Type.NODEVELOPER, ListOfProcesses.Type.TYPICAL);
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

    protected void finished() {
        System.out.println("New processes:");
        for (String name : processes) {
            if (!ListOfProcesses.Type.BLACK_LIST.getList().contains(name) &&
                    !ListOfProcesses.Type.NODEVELOPER.getList().contains(name) &&
                    !ListOfProcesses.Type.COMMUNICATION.getList().contains(name) &&
                    !ListOfProcesses.Type.TYPICAL.getList().contains(name)) {
                LOGGER.warn(name);
            }
        }
    }
}