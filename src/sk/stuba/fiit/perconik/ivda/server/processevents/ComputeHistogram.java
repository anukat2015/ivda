package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.ibm.icu.util.Calendar;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramByList;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Seky on 1. 11. 2014.
 */
public final class ComputeHistogram extends ProcessEvents {
    private final Histogram<Date> hDni = new HistogramByList<>();
    private static final File statsDir = new File(Configuration.CONFIG_DIR);

    public ComputeHistogram() {
        LOGGER.info("Starting computing");
    }

    @Override
    protected void proccessItem(EventDto event) {
        Calendar c = Calendar.getInstance();
        c.setTime(event.getTimestamp());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        hDni.map(c.getTime());
    }

    @Override
    public void finished() {
        saveToFile("hDni.txt", hDni);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void saveToFile(String fileName, Histogram hist) {
        try {
            File file = new File(statsDir, fileName);
            Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            Iterator<Map.Entry<Date, Object>> zoznam = hist.reduce();
            output.write("[['Date', 'Count'],");
            while (zoznam.hasNext()) {
                Map.Entry<Date, Object> entry = zoznam.next();
                output.write("[" + entry.getKey().getTime() + ",\t" + entry.getValue() + "],\n");
            }
            output.flush();
            output.close();
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
        }
    }
}
