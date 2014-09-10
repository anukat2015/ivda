package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.server.process.ProcessEventsToDataTable;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * Created by Seky on 7. 8. 2014.
 */
@NotThreadSafe
public class ProcessAllFinishedProcesses extends ProcessEventsToDataTable {
    private final FindFinishedProcess finishedProcess;

    public ProcessAllFinishedProcesses() {
        finishedProcess = new FindFinishedProcess() {
            @Override
            protected void found(FinishedProcess process) {
                dataTable.add("A", process.start, process.end, MyDataTable.ClassName.AVAILABLE, process.process.getName(), null);
            }
        };
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof ProcessesChangedSinceCheckEventDto)) return;
        ProcessesChangedSinceCheckEventDto cevent = (ProcessesChangedSinceCheckEventDto) event;
        finishedProcess.handle(cevent);
    }

    public void finished() {
        finishedProcess.flushUnfinished();
    }

}
