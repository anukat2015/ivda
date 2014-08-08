package sk.stuba.fiit.perconik.ivda.uaca.client;

import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

import java.io.Serializable;

/**
 * Created by Seky on 20. 7. 2014.
 * <p/>
 * Odpoved vratena z UACA sluzby.
 */
public final class EventsResponse extends PagedResponse<EventDto> implements Serializable {
    public EventsResponse() {
    }
}
