package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;

import java.util.Scanner;

/**
 * Created by Seky on 21. 8. 2014.
 * <p>
 * Vypis event, kde pouzivatel upravoval kod.
 */
public final class ProcessCodeWritten extends ProcessEventsToDataTable {
    protected static final Logger LOGGER = Logger.getLogger(ProcessCodeWritten.class.getName());
    private static final long serialVersionUID = -1342729375850378231L;

    public ProcessCodeWritten(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) {
            throw new IllegalArgumentException("Prisiel zly event.");
        }
        IdeCodeEventDto cevent = (IdeCodeEventDto) event;
        LOGGER.info(cevent);

        dataTable.add(
                event.getUser(),
                event.getTimestamp(),
                EventsUtil.event2Classname(event),
                EventsUtil.event2name(event),
                Integer.toString(computeSize(cevent))
        );
    }

    private int computeSize(IdeCodeEventDto cevent) {
        String txt = cevent.getText();
        if (txt == null) {
            return 0;
        }
        int count = 0;
        Scanner scanner = new Scanner(txt);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            LOGGER.info(line);
            count++;
        }
        scanner.close();
        return count;
    }
}