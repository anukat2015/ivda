package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.io.Files;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;
import sk.stuba.fiit.perconik.ivda.util.lang.TimeGranularity;
import sk.stuba.fiit.perconik.ivda.util.serialize.IterateOutputStreamForFiles;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Seky on 6. 11. 2014.
 * Pomcna trieda pre stiahnutie chunkov.
 */
@NotThreadSafe
public class BankOfChunks {
    private static final Logger LOGGER = Logger.getLogger(BankOfChunks.class.getName());
    private static final File DIR = new File(Configuration.CONFIG_DIR, "dump");
    private static final TimeGranularity granularity = TimeGranularity.DAY;

    public static void processChunks(Date start, Date end) throws IOException {
        Date temp = start;
        while (!temp.equals(end)) {
            temp = granularity.increment(start);
            if (temp.after(end)) {
                temp = end;
            }
            processChunk(start, temp);
            start = temp;
        }
    }

    private static void processChunk(Date start, Date end) throws IOException {
        String name = DateUtils.toString(start).substring(0, 10) + '_' + DateUtils.toString(end).substring(0, 10) + ".gzip";
        File file = new File(DIR, name);
        if (file.exists()) {
            LOGGER.warn("Already exist, skipping: " + name);
            return;
        }

        LOGGER.warn("Downloading:" + name);
        EventsRequest request = new EventsRequest();
        request.setTime(start, end);
        ImmutableList<EventDto> response = ActivityService.getInstance().getEvents(request);
        saveChunk(file, response);
        LOGGER.warn("End");
    }

    private static void saveChunk(File file, ImmutableList<EventDto> response) throws IOException {
        ObjectOutput out = GZIP.getIterativeOutput(file);
        for (EventDto e : response) {
            out.writeObject(e);
        }
        out.close();
    }

    public static List<File> loadChunks(Date start, Date end) {
        Date requestStart = org.apache.commons.lang.time.DateUtils.truncate(start, Calendar.DAY_OF_MONTH);
        Date requestEnd = org.apache.commons.lang.time.DateUtils.truncate(end, Calendar.DAY_OF_MONTH);
        boolean truncated = end.compareTo(requestEnd) != 0;

        String[] names;
        Date itemStart, itemEnd;
        File[] list = DIR.listFiles();
        List<File> touched = new ArrayList<>();
        try {
            for (File file : list) {
                names = Files.getNameWithoutExtension(file.getName()).split("_");
                itemStart = DateUtils.fromString(names[0] + "T00:00:00.000Z");
                itemEnd = DateUtils.fromString(names[1] + "T00:00:00.000Z");

                // Check if is touching

                if (itemStart.compareTo(requestEnd) == 0) {
                    // Berieme do uvahy aj tento posledny element, lebo pri orezani potrebujeme aj tieto data
                    if (truncated) {
                        touched.add(file);
                    }
                    break;
                }
                if (itemStart.compareTo(requestStart) == 0 || DateUtils.isOverlaping(itemStart, itemEnd, requestStart, requestEnd)) {
                    touched.add(file);
                }
            }
        } catch (ParseException ex) {
            LOGGER.error("Cannot parse:" + ex);
        }
        return touched;
    }

    public static Iterator<EventDto> getEvents(Date start, Date end) {
        return new IterateEvents(start, end);
    }

    public static Iterator<EventDto> getEvents(Date start, Date end, final String user) {
        return Iterators.filter(getEvents(start, end), new Predicate<EventDto>() {
            @Override
            public boolean apply(@Nullable EventDto input) {
                return input.getUser().equals(user);
            }
        });
    }


    private static class IterateEvents implements Iterator<EventDto> {
        private static final Logger LOGGER = Logger.getLogger(IterateEvents.class.getName());
        private final Date start;
        private final Date end;
        private final Iterator<Object> savedObjects;
        private EventDto actual = null;

        private IterateEvents(Date start, Date end) {
            this.start = start;
            this.end = end;
            List<File> files = null;
            files = loadChunks(start, end);
            savedObjects = new IterateOutputStreamForFiles(files);
        }

        @Override
        public boolean hasNext() {
            if (actual != null) {
                return true; //we have stored item
            }

            // if there is no stired item, try find another
            EventDto temp;
            Date time;
            while (savedObjects.hasNext()) {
                temp = (EventDto) savedObjects.next();
                time = temp.getTimestamp();
                if (time.after(start) && time.before(end)) {
                    actual = temp;
                    return true;
                }
            }

            return false;
        }

        @Override
        public EventDto next() {
            EventDto ret = actual;
            actual = null; // remove stored item
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
