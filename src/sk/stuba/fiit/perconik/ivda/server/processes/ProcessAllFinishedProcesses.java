package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * Created by Seky on 7. 8. 2014.
 */
@NotThreadSafe
public class ProcessAllFinishedProcesses extends ProcessEventsToDataTable {
    private final FindFinishedProcess finishedProcess;

    public ProcessAllFinishedProcesses(EventsRequest request) {
        super(request);
        finishedProcess = new FindFinishedProcess() {
            @Override
            protected void found(FinishedProcess process) {
                dataTable.add("A", process.start, process.end, MyDataTable.ClassName.AVAILABLE, process.process.getName());
            }
        };
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof ProcessesChangedSinceCheckEventDto)) return;
        ProcessesChangedSinceCheckEventDto cevent = (ProcessesChangedSinceCheckEventDto) event;
        finishedProcess.handle(cevent);
    }

    protected void finished() {
        finishedProcess.flushUnfinished();
    }

}
