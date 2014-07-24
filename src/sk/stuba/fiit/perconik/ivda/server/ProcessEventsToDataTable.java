package sk.stuba.fiit.perconik.ivda.server;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.Client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.Client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.Client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.Client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.dto.EventDto;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class ProcessEventsToDataTable extends DownloadAll<EventDto> {
    private static final Logger logger = Logger.getLogger(ProcessEventsToDataTable.class.getName());

    private int counter = 0;
    private MyDataTable dataTable;

    public ProcessEventsToDataTable(EventsRequest uri) {
        super(EventsResponse.class);
        dataTable = new MyDataTable();
        downloadedNonRecursive(uri.getURI());
    }

    @Override
    protected boolean downloaded(PagedResponse<EventDto> response) {
        counter++;
        //if (counter == 3) return false;

        try {
            for (EventDto event : response.getResultSet()) {
                proccessItem(event);
            }
        } catch (TypeMismatchException e) {
            logger.error("Type mismatch", e);
        }
        return true;   // nechceme dalej stahovat
    }

    private void proccessItem(EventDto event) throws TypeMismatchException {
        String action = event.getActionName();
        String user = event.getUser();
        String eventid = event.getEventId();
        String workstation = event.getWorkstation();

        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().toGregorianCalendar().getTime());

        GregorianCalendar later = (GregorianCalendar) timestamp.clone();
        later.add(GregorianCalendar.SECOND, 10);
        String description = action + "|" + eventid + "|" + workstation;

        dataTable.add(user, timestamp, later, MyDataTable.ClassName.AVAILABLE, description);
    }

    public MyDataTable getDataTable() {
        return dataTable;
    }
};

