package sk.stuba.fiit.perconik.ivda.activity.client;

import junit.framework.TestCase;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.util.Date;
import java.util.List;

public class ActivityServiceTest extends TestCase {

    public void testGetEvent() throws Exception {
        EventDto dto = ActivityService.getInstance().getEvent();
        Assert.assertNotNull(dto);
    }

    public void testGetEvents() throws Exception {
        EventsRequest request = new EventsRequest();
        Date start = DateUtils.fromString("2014-06-01T08:00:00.000Z");
        Date end = DateUtils.fromString("2014-10-03T16:00:00.000Z");
        request.setTime(start, end);

        List<EventDto> resposne = ActivityService.getInstance().getEvents(request);
        Assert.assertNotNull(resposne);
        Assert.assertTrue(resposne.isEmpty());
    }
}