package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.web.WebTabEventDto;

/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessJobEvents extends ProcessEventsToDataTable {
    public ProcessJobEvents(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) throws TypeMismatchException {
        String action = event.getActionName();
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().getTime());
        String description = action +
                "<span class=\"more\"><pre>"
                + event + "<br/>"
                + "</pre></span>";

        if (event instanceof WebTabEventDto) {
            dataTable.add(event.getUser(), timestamp, MyDataTable.ClassName.AVAILABLE, description);
        }

        if (event instanceof IdeEventDto) {
            dataTable.add(event.getUser(), timestamp, MyDataTable.ClassName.MAYBE, description);
        }
    }
}
