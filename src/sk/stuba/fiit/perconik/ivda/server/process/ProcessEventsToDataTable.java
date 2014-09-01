package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.IProcessDownloaded;
import sk.stuba.fiit.perconik.ivda.activity.entities.PagedResponse;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
@NotThreadSafe
public abstract class ProcessEventsToDataTable implements IProcessDownloaded<EventDto> {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    protected final MyDataTable dataTable;

    protected ProcessEventsToDataTable() {
        dataTable = new MyDataTable();
    }

    @Override
    public boolean isDownloaded(PagedResponse<EventDto> response) {
        response.getResultSet().forEach(this::proccessItem);
        return true;   // chceme dalej stahovat
    }

    protected abstract void proccessItem(EventDto event);

    @NotNull
    public MyDataTable getDataTable() {
        return dataTable;
    }
}

