package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.uaca.client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

import java.io.File;

/**
 * Created by Seky on 22. 7. 2014.
 */
public abstract class ProcessEventsToDataTable extends DownloadAll<EventDto> {
    protected static final Logger logger = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    private final static File cacheFolder = new File("C:/cache/");
    protected MyDataTable dataTable;
    private EventsRequest request;


    public ProcessEventsToDataTable(EventsRequest request) {
        super(EventsResponse.class);
        this.request = request;
        dataTable = new MyDataTable();
    }

    @Override
    protected boolean downloaded(PagedResponse<EventDto> response) {
        try {
            for (EventDto event : response.getResultSet()) {
                proccessItem(event);
            }
        } catch (TypeMismatchException e) {
            logger.error("Type mismatch", e);
        }
        return true;   // chceme dalej stahovat
    }

    protected abstract void proccessItem(EventDto event) throws TypeMismatchException;

    public MyDataTable getDataTable() {
        return dataTable;
    }

    public void start() {
        downloadedNonRecursive(request.getURI());
    }
};

