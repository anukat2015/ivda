package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

import java.io.File;

public class PrintProcessesTest extends TestCase {
    private static final File tempFile = new File("C:\\events_rok.gzip");

    public void testFinished() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(tempFile);
        ProcessEvents p = new PrintProcesses();
        p.downloaded(response);
    }
}
