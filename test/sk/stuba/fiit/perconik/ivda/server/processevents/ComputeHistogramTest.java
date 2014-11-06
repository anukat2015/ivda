package sk.stuba.fiit.perconik.ivda.server.processevents;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import java.util.Date;
import java.util.Iterator;

public class ComputeHistogramTest extends TestCase {

    public void testProccessItem() throws Exception {
        Configuration.getInstance();

        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        ComputeHistogram p = new ComputeHistogram();
        p.proccess(it);
    }
}
