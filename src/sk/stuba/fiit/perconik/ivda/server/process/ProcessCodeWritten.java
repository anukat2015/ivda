package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Vypis event, kde pouzivatel upravoval kod.
 */
@NotThreadSafe
public final class ProcessCodeWritten extends ProcessEventsToDataTable {
    protected static final Logger LOGGER = Logger.getLogger(ProcessCodeWritten.class.getName());


    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) {
            throw new IllegalArgumentException("Prisiel zly event.");
        }
        IdeCodeEventDto cevent = (IdeCodeEventDto) event;
        //LOGGER.info(cevent);
        int size = EventsUtil.codeWritten(cevent.getText());
        if (size > 0) {
            // Ignorujeme ziadne zmeny v kode
            dataTable.addEvent(event, Integer.valueOf(size));
        }
    }


}