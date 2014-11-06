package sk.stuba.fiit.perconik.ivda.activity.client;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import java.io.File;
import java.util.Date;

/**
 * Otestovanie funkcionality Activity sluzby
 */
public class ActivityServiceTest extends TestCase {
    public static final File FILE_EVENTS_ROK = new File(Configuration.CONFIG_DIR, "events_2014.gzip");

    /**
     * Skontroluj deserializaciu a stiahnutie konkretneho eventu
     *
     * @throws Exception
     */
    public void testGetEvent() throws Exception {
        Configuration.getInstance();
        EventDto dto = ActivityService.getInstance().getEvent("68119af1-ebc3-4dc6-bab5-b6a7b1f03aad");
        Assert.assertNotNull(dto);
    }

    /**
     * Skontroluj deserializaciu a stiahnutie eventov v ramci obdobia
     *
     * @throws Exception
     */
    public void testGetEvents() throws Exception {
        Configuration.getInstance();
        EventsRequest request = new EventsRequest();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-03-01T00:00:00.000Z");
        request.setTime(start, end);

        ImmutableList<EventDto> response = ActivityService.getInstance().getEvents(request);
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());

        try {
            GZIP.serialize(response, FILE_EVENTS_ROK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
