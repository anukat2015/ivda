package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.visualization.datasource.base.TypeMismatchException;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ApplicationEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.MonitoringStartedEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.web.WebEventDto;

/**
 * Created by Seky on 8. 8. 2014.
 */
public class ProcessAsGroup extends ProcessEventsToDataTable {
    /**
     * tzv. raz za minutu sa posle event, vtedy vieme urcite ze je aktivny
     */
    private static final int ACTIVITY_MIN_INTERVAL = 1000 * 60 * 1;

    /**
     * tzv. raz za 5 min s amusi poslat udaj o procesoch, vtedy sa da chapat, ze je na pocitaci ale eventy sa nezachytavaju
     * napriklad pzoera film
     */
    private static final int PROCESS_MIN_INTERVAL = 1000 * 60 * 5;

    public ProcessAsGroup(EventsRequest request) {
        super(request);
        if (!request.getAscending()) {
            throw new IllegalArgumentException("Ascening should be true");
        }
    }

    private EventDto lastEvent = null;

    @Override
    protected void proccessItem(EventDto event) throws TypeMismatchException {
        if (event instanceof MonitoringStartedEventDto) {
            return; // ignoruj
        }
        if (event instanceof WebEventDto || event instanceof IdeEventDto) {
            lastEvent = event;
            lastEvent = ((ApplicationEventDto) event).getSessionId();
        }
        dataTable.add(event.getUser(), timestamp, MyDataTable.ClassName.AVAILABLE, content, description);
    }
}
