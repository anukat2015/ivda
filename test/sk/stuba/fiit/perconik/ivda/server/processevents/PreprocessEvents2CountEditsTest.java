package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityServiceTest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.filestats.PreprocessEvents2CountEdits;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

public class PreprocessEvents2CountEditsTest extends TestCase {
    public void testProccessList() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(ActivityServiceTest.FILE_EVENTS_ROK);

        ProcessEvents p = new PreprocessEvents2CountEdits();
        p.downloaded(response);
    }
}
