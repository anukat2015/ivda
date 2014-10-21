package sk.stuba.fiit.perconik.ivda.server.processes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 21. 10. 2014.
 */
public class FinishedProcessForUser extends FindFinishedProcessForUser {
    private final List<FinishedProcess> processes;

    public FinishedProcessForUser() {
        processes = new ArrayList<>();
    }

    @Override
    protected void found(FinishedProcess process) {
        processes.add(process);
    }

    /**
     * Vycisti ukoncene a neukoncene procesy
     */
    @Override
    public void clear() {
        processes.clear();
        super.clear();
    }
}
