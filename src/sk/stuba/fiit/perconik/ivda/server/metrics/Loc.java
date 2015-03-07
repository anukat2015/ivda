package sk.stuba.fiit.perconik.ivda.server.metrics;

import com.google.common.base.Strings;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 7. 3. 2015.
 * Implementacia LOC metriky
 * Zdrojovy kod musi obsahovat aspon jeden neprazdny riadok s kodom.
 */
public final class Loc implements SourceCodeMetric {
    private static final Pattern FULL_LINE_PATTERN = Pattern.compile("[^\\s]+");

    @Override
    public int eval(String txt) {
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
