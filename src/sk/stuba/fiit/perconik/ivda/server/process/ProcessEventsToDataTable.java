package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
@NotThreadSafe
public abstract class ProcessEventsToDataTable {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    protected final MyDataTable dataTable;
    protected TimelineRequest filter;

    protected ProcessEventsToDataTable() {
        dataTable = new MyDataTable();
        filter = null;
    }

    public void downloaded(List<EventDto> list) {
        for (EventDto event : list) {
            filterItem(event);
        }
        finished();
    }

    protected abstract void proccessItem(EventDto event);

    protected void filterItem(EventDto event) {
        if (filter != null) {
            // Filter pre developerov
            if (!filter.containDeveloper(event.getUser())) {
                return;
            }
        }
        proccessItem(event);
    }

    @NotNull
    public MyDataTable getDataTable() {
        return dataTable;
    }

    public void setFilter(TimelineRequest filter) {
        this.filter = filter;
    }

    public void finished() {
    }
}

