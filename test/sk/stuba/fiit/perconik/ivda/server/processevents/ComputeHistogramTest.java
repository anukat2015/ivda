package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityServiceTest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

public class ComputeHistogramTest extends TestCase {

    public void testProccessItem() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(ActivityServiceTest.FILE_EVENTS_ROK);


        ComputeHistogram p = new ComputeHistogram();
        p.downloaded(response);
    }
}
