package sk.stuba.fiit.perconik.ivda.server.process;

import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;


/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessAllEvents extends ProcessEventsToDataTable {
    public ProcessAllEvents(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) {
        String action = event.getActionName();
        String description = "<span class=\"more\"><pre>"
                + event + "<br/>"
                + "</pre></span>";
        dataTable.add(event.getUser(), event.getTimestamp(), MyDataTable.ClassName.AVAILABLE, action, description);
    }
}
