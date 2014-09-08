package sk.stuba.fiit.perconik.ivda.server;


import com.google.common.base.Strings;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ProcessesChangedSinceCheckEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.web.WebEventDto;

import javax.annotation.concurrent.ThreadSafe;
import javax.validation.constraints.NotNull;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 16. 8. 2014.
 * <p>
 * Pomocna trieda pre pracovanie s EventDto
 */
@ThreadSafe
public final class EventsUtil {
    private static final Pattern FULL_LINE_PATTERN = Pattern.compile("[^\\s]+");

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
            Matcher m = FULL_LINE_PATTERN.matcher(line);
            if (m.find()) {
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
