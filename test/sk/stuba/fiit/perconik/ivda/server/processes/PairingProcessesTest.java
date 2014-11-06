package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

public class PairingProcessesTest extends TestCase {
    private static final File processesFile = new File(Configuration.CONFIG_DIR, "processes.gzip");

    /**
     * Tento test spusti funkcionality PairingProcessesTest, nasledne vytiahne procesy pre pouzivatela a ulozi ich.
     *
     * @throws Exception
     */
    public void testProccessItem() throws Exception {
        Configuration.getInstance();

        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        PairingProcesses p = new PairingProcesses();
        p.proccess(it);
        //p.printInfo();
        p.clearAllUnfinished();

        ImmutableMap<String, PerUserProcesses> map = p.getUserMapping();
        GZIP.serialize(map, processesFile);
    }
}
