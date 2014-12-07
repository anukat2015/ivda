package sk.stuba.fiit.perconik.ivda.server.grouping;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.server.processevents.Array2Json;
import sk.stuba.fiit.perconik.ivda.server.servlets.IvdaEvent;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

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

        ProcessIterator<EventDto> process = new ProcessAsGroup() {
            private final Array2Json out = new Array2Json(new FileOutputStream(FILE, false));

            @Override
            public void finished() {
                super.finished();
                out.close();
            }

            @Override
            protected void started() {
                out.start();
                super.started();
            }

            @Override
            protected void foundEndOfGroup(Group group) {
                // Ked bol prave jeden prvok v odpovedi firstEvent a lastEvent je to iste
                EventDto first = group.getFirstEvent();
                EventDto last = group.getLastEvent();

                // Store event
                IvdaEvent event = new IvdaEvent();
                event.setStart(first.getTimestamp());
                event.setEnd(last.getTimestamp());
                event.setGroup(EventsUtil.event2name(first));
                event.setY(group.countEvents());
                out.write(event);
            }
        };
        process.proccess(it);
    }
}
