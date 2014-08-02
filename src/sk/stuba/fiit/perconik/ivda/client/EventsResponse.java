package sk.stuba.fiit.perconik.ivda.client;

import sk.stuba.fiit.perconik.ivda.dto.EventDto;

import java.io.Serializable;

/**
 * Created by Seky on 20. 7. 2014.
 */
public final class EventsResponse extends PagedResponse<EventDto> implements Serializable {
    public EventsResponse() {
    }
}
