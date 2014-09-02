package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Vypis event, kde pouzivatel upravoval kod.
 */
@NotThreadSafe
public final class ProcessCodeWritten extends ProcessEventsToDataTable {
    protected static final Logger LOGGER = Logger.getLogger(ProcessCodeWritten.class.getName());
    private static final Pattern FULL_LINE_PATTERN = Pattern.compile("[^\\s]+");

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) {
            throw new IllegalArgumentException("Prisiel zly event.");
        }
        IdeCodeEventDto cevent = (IdeCodeEventDto) event;
        //LOGGER.info(cevent);
        int size = computeSize(cevent);
        if (size > 0) {
            // Ignorujeme ziadne zmeny v kode
            dataTable.addEvent(event, Integer.toString(size));
        }
    }

    /**
     * Spocitaj na zaklade poctu riadkov :)
     * Alebo na zaklade poctu znakov?
     *
     * @param cevent
     * @return
     */
    private int computeSize(IdeCodeEventDto cevent) {
        String txt = cevent.getText();
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