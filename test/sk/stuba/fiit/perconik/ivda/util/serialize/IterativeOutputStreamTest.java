package sk.stuba.fiit.perconik.ivda.util.serialize;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import java.io.File;
import java.io.ObjectOutput;

public class IterativeOutputStreamTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(IterativeOutputStreamTest.class.getName());
    private static final File TEST_FILE = new File(Configuration.CONFIG_DIR, "test.gzip");

    public void testWriteObject() throws Exception {
        Configuration.getInstance();
        ObjectOutput out = GZIP.getIterativeOutput(TEST_FILE);
        for (int i = 1; i <= 1000; i++) {
            out.writeObject(new Integer(i));
            //LOGGER.warn(new Integer(i));
        }
        out.close();

        ObjectInputIterator it = GZIP.getIterativeInput(TEST_FILE);
        while (it.hasNext()) {
            Object obj = it.next();
            //System.out.println(obj);
            LOGGER.info(obj);
        }
    }
}
