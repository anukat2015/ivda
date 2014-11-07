package sk.stuba.fiit.perconik.ivda.server.grouping;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEventsOut;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;

public class ProcessAsGroupTest extends TestCase {
    private static final File FILE = new File(Configuration.CONFIG_DIR, "group.txt");

    public void testProccessItem() throws Exception {
        Configuration.getInstance();

        Date start = DateUtils.fromString("2014-01-06T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-10-09T00:00:00.000Z");
        Iterator<EventDto> allEvents = BankOfChunks.getEvents(start, end);
        Iterator<EventDto> it = Iterators.filter(allEvents, new Predicate<EventDto>() {
            @Override
            public boolean apply(@Nullable EventDto input) {
                return input.getUser().equals("steltecia\\krastocny");
            }
        });

        ProcessEventsOut process = new ProcessAsGroup(new FileOutputStream(FILE, false));
        process.proccess(it);
    }
}
