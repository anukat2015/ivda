package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.OutputStream;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Vypis event, kde pouzivatel upravoval kod.
 */
@NotThreadSafe
public final class ProcessAllWrittenCodes extends ProcessEventsOut {

    protected ProcessAllWrittenCodes(OutputStream out) {
        super(out);
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) {
            throw new IllegalArgumentException("Prisiel zly event.");
        }

        IdeCodeEventDto cevent = (IdeCodeEventDto) event;

        int size = EventsUtil.codeWritten(cevent.getText());
        if (size > 0) {
            // Pridam elen ke nastala zmena
            add(event, size);
        }
    }


}
