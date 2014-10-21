package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

import java.io.File;
import java.util.Date;

public class PreprocessEventsTest extends TestCase {

    private static final File tempFile = new File("C:\\events_tyzden.gzip");

    public void testDownload() throws Exception {
        Configuration.getInstance();
        EventsRequest request = new EventsRequest();
        Date start = DateUtils.fromString("2014-08-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-09-01T00:00:00.000Z");
        request.setTime(start, end);

        ImmutableList<EventDto> response = ActivityService.getInstance().getEvents(request);
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());

        try {
            GZIP.serialize(response, tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testProccessList() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = null;
        try {
            response = (ImmutableList<EventDto>) GZIP.deserialize(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreprocessEvents p = new PreprocessEvents();
        p.downloaded(response);

    }
}
