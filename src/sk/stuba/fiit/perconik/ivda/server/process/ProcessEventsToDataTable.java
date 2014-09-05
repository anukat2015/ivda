package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
@NotThreadSafe
public abstract class ProcessEventsToDataTable implements RestClient.IProcessPage<EventsResponse> {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    protected final MyDataTable dataTable;

    protected ProcessEventsToDataTable() {
        dataTable = new MyDataTable();
    }

    @Override
    public void downloaded(EventsResponse response) {
        response.getResultSet().forEach(this::proccessItem);
    }

    protected abstract void proccessItem(EventDto event);

    @NotNull
    public MyDataTable getDataTable() {
        return dataTable;
    }
}

