package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class BankOfChunksTest extends TestCase {

    public void testProcessChunks() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-05T00:00:00.000Z");
        BankOfChunks.processChunks(start, end);
    }

    public void testLoadChunks() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-01-09T00:00:00.000Z");
        List<File> list = BankOfChunks.loadChunks(start, end);
        for (File f : list) {
            System.out.println(f.getName());
        }
    }
}
