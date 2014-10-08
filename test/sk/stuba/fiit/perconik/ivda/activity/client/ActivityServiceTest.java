package sk.stuba.fiit.perconik.ivda.activity.client;

import junit.framework.TestCase;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Otestovanie funkcionality Activity sluzby
 */
public class ActivityServiceTest extends TestCase {

    /**
     * Skontroluj deserializaciu a stiahnutie konkretneho eventu
     *
     * @throws Exception
     */
    public void testGetEvent() throws Exception {
        EventDto dto = ActivityService.getInstance().getEvent("68119af1-ebc3-4dc6-bab5-b6a7b1f03aad");
        Assert.assertNotNull(dto);
    }


    /**
     * Skontroluj deserializaciu a stiahnutie evntov v ramci obdobia
     *
     * @throws Exception
     */
    public void testGetEvents() throws Exception {
        EventsRequest request = new EventsRequest();
        Date start = DateUtils.fromString("2014-06-01T08:00:00.000Z");
        Date end = DateUtils.fromString("2014-07-01T16:00:00.000Z");
        request.setTime(start, end);

        List<EventDto> resposne = ActivityService.getInstance().getEvents(request);
        Assert.assertNotNull(resposne);
        Assert.assertFalse(resposne.isEmpty());
    }
}