package sk.stuba.fiit.perconik.ivda.server.processevents;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import java.util.Iterator;

/**
 * Created by Seky on 21. 10. 2014.
 * Metoda spracovania udalosti.
 */
public abstract class ProcessEvents {
    protected final Logger LOGGER;
    private boolean enabledProcess;

    protected ProcessEvents() {
        LOGGER = Logger.getLogger(this.getClass().getName());
        enabledProcess = true;
    }

    public void proccess(Iterator<EventDto> it) {
        started();
        while (enabledProcess && it.hasNext()) {
            proccessItem(it.next());
        }
        finished();
    }

    protected void stop() {
        enabledProcess = false;
    }

    protected abstract void proccessItem(EventDto event);

    protected void finished() {
    }

    protected void started() {
    }
}
