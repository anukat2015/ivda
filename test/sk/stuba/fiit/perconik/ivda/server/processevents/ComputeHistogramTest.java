package sk.stuba.fiit.perconik.ivda.server.processevents;

import junit.framework.TestCase;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class ComputeHistogramTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(ComputeHistogramTest.class.getName());
    private static final File statsDir = new File(Configuration.CONFIG_DIR);

    public void testProccessItem() throws Exception {
        Configuration.getInstance();

        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        LOGGER.info("Starting computing");
        ComputeHistogram p = new ComputeHistogram();
        p.proccess(it);
        LOGGER.info("Flushing");
        saveToFile("hAutory.txt", p.getHistogram());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void saveToFile(String fileName, Histogram hist) {
        try {
            File file = new File(statsDir, fileName);
            Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            Iterator<Map.Entry<String, MutableInt>> zoznam = hist.reduce();
            //output.write("[['Date', 'Count'],");
            while (zoznam.hasNext()) {
                Map.Entry<String, MutableInt> entry = zoznam.next();
                //output.write("[" + entry.getKey().getTime() + ",\t" + entry.getValue() + "],\n");
                output.write(entry.getKey() + "\t" + entry.getValue() + "\n");
            }
            output.flush();
            output.close();
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
        }
    }
}
