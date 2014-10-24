package sk.stuba.fiit.perconik.ivda.server;


import com.google.common.base.Strings;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;

import javax.annotation.concurrent.ThreadSafe;
import javax.validation.constraints.NotNull;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 16. 8. 2014.
 * <p/>
 * Pomocna trieda pre pracovanie s EventDto
 */
@ThreadSafe
public final class EventsUtil {
    private static final Pattern FULL_LINE_PATTERN = Pattern.compile("[^\\s]+");

    @NotNull
    public static TimelineEvent.ClassName event2Classname(EventDto event) {
        //noinspection IfStatementWithTooManyBranches
        if (event instanceof WebEventDto) {
            return TimelineEvent.ClassName.AVAILABLE;
        } else if (event instanceof IdeEventDto) {
            return TimelineEvent.ClassName.MAYBE;
        } else if (event instanceof ProcessesChangedSinceCheckEventDto) {
            return TimelineEvent.ClassName.AVAILABLE;
        } else {
            return TimelineEvent.ClassName.UNAVAILABLE; // tzv nezanmy typ entity prisiel
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

    /**
     * Spocitaj na zaklade poctu riadkov :)
     * Alebo na zaklade poctu znakov?
     *
     * @param cevent
     * @return
     */
    public static int codeWritten(String txt) {
        if (Strings.isNullOrEmpty(txt)) {
            return 0;
        }

        int count = 0;
        int emptyLines = 0;
        Scanner scanner = new Scanner(txt);
        while (scanner.hasNextLine()) {
            // TRIM sa pouzit nemoze, lebo to meni charakteristiku kodu ...
            String line = scanner.nextLine();
            //LOGGER.info(line);
            Matcher matcher = FULL_LINE_PATTERN.matcher(line);
            if (matcher.find()) {
                count++;
            } else {
                emptyLines++;
            }
        }
        scanner.close();
        if (count == 0) {
            // Zmena neobsahuje aspon jeden normalny riadok
            return 0;
        }
        return count + emptyLines;
    }
}
