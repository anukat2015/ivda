package sk.stuba.fiit.perconik.ivda.server.process;

import sk.stuba.fiit.perconik.ivda.server.processes.FindFinishedProcess;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;


/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessAllProcess extends ProcessEventsToDataTable {
    private final FindFinishedProcess finishedProcess;

    public ProcessAllProcess(EventsRequest request) {
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
