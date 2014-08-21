package sk.stuba.fiit.perconik.ivda.server;


import sk.stuba.fiit.perconik.ivda.uaca.client.WebClient;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.web.WebEventDto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by Seky on 16. 8. 2014.
 * <p>
 * Pomocna trieda pre pracovanie s EventDto
 */
public final class EventsUtil {

    @NotNull
    public static MyDataTable.ClassName event2Classname(EventDto event) {
        //noinspection IfStatementWithTooManyBranches
        if (event instanceof WebEventDto) {
            return MyDataTable.ClassName.AVAILABLE;
        } else if (event instanceof IdeEventDto) {
            return MyDataTable.ClassName.MAYBE;
        } else if (event instanceof ProcessesChangedSinceCheckEventDto) {
            return MyDataTable.ClassName.AVAILABLE;
        } else {
            return MyDataTable.ClassName.UNAVAILABLE; // tzv nezanmy typ entity prisiel
        }
    }

    @NotNull
    public static String event2name(EventDto event) {
        //noinspection IfStatementWithTooManyBranches
        if (event instanceof WebEventDto) {
            return "Web";
        } else if (event instanceof IdeEventDto) {
            return "Ide";
        } else if (event instanceof ProcessesChangedSinceCheckEventDto) {
            return "Iny proces";
        } else {
            return "Unknown";
        }
    }

    public static EventDto download(String id) {
        WebClient client = new WebClient();
        UriBuilder builder = UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
        builder.path(id);
        return (EventDto) client.synchronizedRequest(builder.build(), EventDto.class);
    }

    public static long diffTime(EventDto actual, EventDto last) {
        long actualTimestamp = actual.getTimestamp().getTimeInMillis();
        long lastTimestamp = last.getTimestamp().getTimeInMillis();
        return actualTimestamp - lastTimestamp;
    }
}
