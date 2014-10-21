package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Seky on 7. 8. 2014.
 */
@NotThreadSafe
public class ProcessAllFinishedProcesses extends ProcessEvents {
    private final Map<String, FinishedProcessForUser> finishedProcessForUser; // tzv pre kazdeho pouzivatel

    public ProcessAllFinishedProcesses() {
        finishedProcessForUser = new HashMap<>();
    }

    /**
     * Vyvojar restartoval pc, procesy budu mat nove PID
     */
    protected void restartedSensor(FinishedProcessForUser processes) {
        if (processes == null) {
            return; // este nebol vytvoreny zoznam
        }

        // vycisti ulozeny zoznam
        processes.clear();
    }

    @Override
    protected void proccessItem(EventDto event) {
        String user = event.getUser();
        FinishedProcessForUser processes = finishedProcessForUser.get(user);

        if ((event instanceof MonitoringStartedEventDto)) {
            restartedSensor(processes);
            return;
        }

        if ((event instanceof ProcessesChangedSinceCheckEventDto)) {
            ProcessesChangedSinceCheckEventDto cevent = (ProcessesChangedSinceCheckEventDto) event;

            // Identifikuj podla pouzivatela
            if (processes == null) {
                processes = new FinishedProcessForUser();
                finishedProcessForUser.put(user, processes);
            }

            // Pouzi konkretny procesor
            processes.handle(cevent);
        }
    }

    public void finished() {
        Set<Map.Entry<String, FinishedProcessForUser>> set = finishedProcessForUser.entrySet();
        for (Map.Entry<String, FinishedProcessForUser> entry : set) {
            LOGGER.info("Flushing for " + entry.getKey());
            entry.getValue().flushUnfinished();
        }
    }

}
