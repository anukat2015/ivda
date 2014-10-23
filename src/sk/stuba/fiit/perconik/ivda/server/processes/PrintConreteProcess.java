package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;

/**
 * Created by Seky on 22. 10. 2014.
 * <p/>
 * Metoda spracovania procesov, ktora vyhlada len urcity proces, podla nazvu
 * Metoda je urcena ako pomocka pri analyze dat..
 */
public final class PrintConreteProcess extends ProcessProcesses {
    private final String name;

    public PrintConreteProcess(String name) {
        this.name = name;
    }

    @Override
    protected void handleStarted(ProcessesChangedSinceCheckEventDto event, ProcessDto p) {
        if (p.getName().equals(name)) {
            LOGGER.info("S\t" + p);
        }
    }

    @Override
    protected void handleKilled(ProcessesChangedSinceCheckEventDto event, ProcessDto p) {
        if (p.getName().equals(name)) {
            LOGGER.info("S\t" + p);
        }
    }
}
