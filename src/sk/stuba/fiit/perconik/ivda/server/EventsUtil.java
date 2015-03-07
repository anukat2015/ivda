package sk.stuba.fiit.perconik.ivda.server;


import sk.stuba.fiit.perconik.ivda.activity.dto.BashCommandEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.metrics.Loc;
import sk.stuba.fiit.perconik.ivda.server.metrics.SourceCodeMetric;

import javax.annotation.concurrent.ThreadSafe;
import javax.validation.constraints.NotNull;

/**
 * Created by Seky on 16. 8. 2014.
 * <p/>
 * Pomocna trieda pre pracovanie s EventDto
 */
@ThreadSafe
public final class EventsUtil {
    private static final SourceCodeMetric metric = new Loc();

    @NotNull
    public static String event2name(EventDto event) {
        //noinspection IfStatementWithTooManyBranches
        if (event instanceof WebEventDto) {
            return "Web";
        } else if (event instanceof IdeEventDto) {
            return "Ide";
        } else if (event instanceof BashCommandEventDto) {
            return "Bash";
        } else if (event instanceof ProcessesChangedSinceCheckEventDto) {
            return "Iny proces";
        } else {
            return "Unknown";
        }
    }

    public static int codeWritten(String txt) {
        return metric.eval(txt);
    }
}
