package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BankOfChunksTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(BankOfChunksTest.class.getName());

    public void testProcessChunks() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-04T00:00:00.000Z");
        BankOfChunks.processChunks(start, end);
    }

    public void testLoadChunks() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        List<File> list = BankOfChunks.loadChunks(start, end);
        for (File f : list) {
            System.out.println(f.getName());
        }
    }

    public void testGetEvents() throws Exception {
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-07-00T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        while (it.hasNext()) {
            EventDto dto = it.next();
            LOGGER.info(dto);
        }
    }
}
