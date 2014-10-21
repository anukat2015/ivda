package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents2TimelineEvents;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * Created by Seky on 7. 8. 2014.
 */
@NotThreadSafe
public class ProcessAllFinishedProcesses extends ProcessEvents2TimelineEvents {
    private final FindFinishedProcess finishedProcess;

    public ProcessAllFinishedProcesses() {
        finishedProcess = new FindFinishedProcess() {
            @Override
            protected void found(FinishedProcess process) {
                add(new TimelineEvent(process.start, "A", TimelineEvent.ClassName.AVAILABLE, process.process.getName(), process.end, null));
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
