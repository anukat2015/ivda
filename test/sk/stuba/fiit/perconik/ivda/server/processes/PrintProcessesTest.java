package sk.stuba.fiit.perconik.ivda.server.processes;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

import java.util.Date;
import java.util.Iterator;

public class PrintProcessesTest extends TestCase {

    public void testFinished() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        ProcessIterator<EventDto> p = new PrintProcesses();
        p.proccess(it);
    }
}
