package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ApplicationEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessAllEvents extends ProcessEventsToDataTable {
    public ProcessAllEvents(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) throws TypeMismatchException {
        String action = event.getActionName();
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().getTime());
        String content = action +
                "<span class=\"more\"><pre>"
                + event + "<br/>"
                + "</pre></span>";

        String description = null;
        if(event instanceof ApplicationEventDto) {
            description = ((ApplicationEventDto) event).getSessionId();
        }
        dataTable.add(event.getUser(), timestamp, MyDataTable.ClassName.AVAILABLE, content, description);
    }
}
