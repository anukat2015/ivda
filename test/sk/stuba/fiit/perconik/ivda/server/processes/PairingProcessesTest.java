package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

import java.io.File;

public class PairingProcessesTest extends TestCase {
    private static final File tempFile = new File("C:\\events_rok.gzip");
    private static final File processesFile = new File("C:\\processes.gzip");

    public void testProccessItem() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(tempFile);

        PairingProcesses p = new PairingProcesses();
        p.downloaded(response);
        p.printInfo();

        ImmutableList<Process> processes = p.getFinishedProcesses("steltecia\\krastocny");
        GZIP.serialize(processes, processesFile);
    }
}
