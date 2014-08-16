package sk.stuba.fiit.perconik.ivda.server;

import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.web.WebEventDto;

import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 16. 8. 2014.
 */
public final class EventsUtil {

    public static
    @NotNull
    MyDataTable.ClassName event2Classname(EventDto event) {
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

    public static
    @NotNull
    String event2name(EventDto event) {
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
}
