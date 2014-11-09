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
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;
import sk.stuba.fiit.perconik.ivda.util.serialize.IterateOutputStreamForFiles;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Seky on 6. 11. 2014.
 * Pomcna trieda pre stiahnutie chunkov.
 */
@NotThreadSafe
public class BankOfChunks {
    private static final Logger LOGGER = Logger.getLogger(BankOfChunks.class.getName());
    private static final File DIR = new File(Configuration.CONFIG_DIR, "dump");
    private static final int SIZE_OF_CHUNK = 1000 * 60 * 60 * 24; // 1 day

    private static final ThreadLocal<DateFormat> FORMATTER = new ThreadLocal<DateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
            format.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            return format;
        }
    };

    public static void processChunks(Date start, Date end) throws IOException {
        Date temp = start;
        while (!temp.equals(end)) {
            temp = new Date(start.getTime() + SIZE_OF_CHUNK);
            if (temp.after(end)) {
                temp = end;
            }
            processChunk(start, temp);
            start = temp;
        }
    }

    private static void processChunk(Date start, Date end) throws IOException {
        String name = FORMATTER.get().format(start) + '-' + FORMATTER.get().format(end) + ".gzip";
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

    public static List<File> loadChunks(Date start, Date end) throws ParseException {
        String[] dates;
        Date itemStart, itemEnd;
        File[] list = DIR.listFiles();
        List<File> touched = new ArrayList<>();
        for (File file : list) {
            dates = Files.getNameWithoutExtension(file.getName()).split("-");
            itemStart = FORMATTER.get().parse(dates[0]);
            itemEnd = FORMATTER.get().parse(dates[1]);

            // Check if is touching
            if ((start.getTime() <= itemStart.getTime() && itemStart.getTime() <= end.getTime()) || (start.getTime() <= itemEnd.getTime() && itemEnd.getTime() <= end.getTime())) {
                touched.add(file);
            }
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
            try {
                files = loadChunks(start, end);
            } catch (ParseException e) {
                LOGGER.error("Cannot parse:" + e);
            }
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
