package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.util.Catalog;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * Created by Seky on 7. 8. 2014.
 * Metoda spracovania udalosti, ktora rozsiruje svoju funkcionalitu spracovania udalosti
 * tak, ze sa zameriava iba na procesy spustene a ukoncene u pouzivatela.
 */
@NotThreadSafe
public abstract class ProcessProcesses extends ProcessIterator<EventDto> {
    private final Catalog appBlackList;

    protected ProcessProcesses() {
        appBlackList = Catalog.Processes.BANNED.getList();
    }

    /**
     * Vyvojar restartoval pc, procesy budu mat nove PID
     */
    protected void restartProccesses(String user) {

    }

    @Override
    protected void proccessItem(EventDto event) {
        String user = event.getUser();

        if ((event instanceof MonitoringStartedEventDto)) {
            restartProccesses(user);
            return;
        }

        if ((event instanceof ProcessesChangedSinceCheckEventDto)) {
            // Pouzi konkretny procesor
            handleProcessesChanged((ProcessesChangedSinceCheckEventDto) event);
        }
    }

    /**
     * Skontroluj zoznam procesov.
     *
     * @param event
     */
    public final void handleProcessesChanged(ProcessesChangedSinceCheckEventDto event) {
        handleStarted(event);
        handleKilled(event);
    }

    protected abstract void handleStarted(ProcessesChangedSinceCheckEventDto event, ProcessDto started);

    private void handleStarted(ProcessesChangedSinceCheckEventDto event) {
        for (ProcessDto started : event.getStartedProcesses()) {
            if (appBlackList.exist(started)) {
                continue;
            }
            handleStarted(event, started);
        }
    }

    private void handleKilled(ProcessesChangedSinceCheckEventDto event) {
        for (ProcessDto killed : event.getKilledProcesses()) {
            handleKilled(event, killed);
        }
    }

    protected abstract void handleKilled(ProcessesChangedSinceCheckEventDto event, ProcessDto killed);
}
