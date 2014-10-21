package sk.stuba.fiit.perconik.ivda.server.processevents;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

import java.util.List;

/**
 * Created by Seky on 21. 10. 2014.
 */
public abstract class ProcessEvents {
    protected final Logger LOGGER;

    protected ProcessEvents() {
        LOGGER = Logger.getLogger(this.getClass().getName());
    }

    public void downloaded(List<EventDto> list) {
        started();
        for (EventDto event : list) {
            filterItem(event);
        }
        finished();
    }

    protected abstract void proccessItem(EventDto event);

    protected void filterItem(EventDto event) {
        proccessItem(event);
    }

    public void finished() {
    }

    protected void started() {
    }
}
