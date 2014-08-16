package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.uaca.client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
public abstract class ProcessEventsToDataTable extends DownloadAll<EventDto> {
    protected static final Logger LOGGER = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    private static final long serialVersionUID = 6600835762437333632L;
    protected final MyDataTable dataTable;
    private final EventsRequest request;


    protected ProcessEventsToDataTable(EventsRequest myrequest) {
        super(EventsResponse.class);
        request = myrequest;
        dataTable = new MyDataTable();
    }

    @Override
    protected boolean isDownloaded(PagedResponse<EventDto> response) {
        try {
            for (EventDto event : response.getResultSet()) {
                proccessItem(event);
            }
        } catch (TypeMismatchException e) {
            LOGGER.error("Type mismatch", e);
        }
        return true;   // chceme dalej stahovat
    }

    protected abstract void proccessItem(EventDto event) throws TypeMismatchException;

    @NotNull
    public MyDataTable getDataTable() {
        return dataTable;
    }

    public void start() {
        try {
            downloadedNonRecursive(request.getURI());
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu.", e);
        }
    }
}

