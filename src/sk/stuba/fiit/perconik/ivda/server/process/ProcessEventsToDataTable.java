package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.uaca.client.PagedResponse;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * Trieda, ktora stiahne vsetky Eventy a je na rozsireni tejto triedy ako sa spracuju dane eventy do datatable.
 */
@NotThreadSafe
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
        response.getResultSet().forEach(this::proccessItem);
        return true;   // chceme dalej stahovat
    }

    protected abstract void proccessItem(EventDto event);

    @NotNull
    public MyDataTable getDataTable() {
        return dataTable;
    }

    public void start() {
        try {
            downloadedNonRecursive(request.getURI());
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
    }
}

