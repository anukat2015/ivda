package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityServiceTest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

import java.io.File;

public class PairingProcessesTest extends TestCase {
    private static final File processesFile = new File("C:\\processes.gzip");

    /**
     * Tento test spusti funkcionality PairingProcessesTest, nasledne vytiahne procesy pre pouzivatela a ulozi ich.
     *
     * @throws Exception
     */
    public void testProccessItem() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(ActivityServiceTest.FILE_EVENTS_ROK);

        PairingProcesses p = new PairingProcesses();
        p.downloaded(response);
        p.printInfo();

        ImmutableList<Process> processes = p.getFinishedProcesses("steltecia\\krastocny");
        GZIP.serialize(processes, processesFile);
    }
}
