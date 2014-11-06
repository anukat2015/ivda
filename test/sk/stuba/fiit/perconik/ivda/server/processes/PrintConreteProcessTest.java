package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityServiceTest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

public class PrintConreteProcessTest extends TestCase {

    /**
     * Metoda na spustenie a overenie funkcionality pre hladanie konkretne procesu podla nazvu.
     *
     * @throws Exception
     */
    public void testHandleAll() throws Exception {
        Configuration.getInstance();
        ImmutableList<EventDto> response = (ImmutableList<EventDto>) GZIP.deserialize(ActivityServiceTest.FILE_EVENTS_ROK);
        ProcessEvents p = new PrintConreteProcess("MendeleyDesktop");
        p.downloaded(response);
    }
}
