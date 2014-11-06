package sk.stuba.fiit.perconik.ivda.server.processevents;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.BankOfChunks;
import sk.stuba.fiit.perconik.ivda.server.filestats.FilesOperationsRepository;
import sk.stuba.fiit.perconik.ivda.server.filestats.PreprocessEvents2CountEdits;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

public class PreprocessEvents2CountEditsTest extends TestCase {
    private static final File operationsFile = new File(Configuration.CONFIG_DIR, "fileOperations.gzip");

    public void testProccessList() throws Exception {
        Configuration.getInstance();
        Date start = DateUtils.fromString("2014-01-01T00:00:00.000Z");
        Date end = DateUtils.fromString("2014-11-09T00:00:00.000Z");
        Iterator<EventDto> it = BankOfChunks.getEvents(start, end);

        PreprocessEvents2CountEdits p = new PreprocessEvents2CountEdits();
        p.proccess(it);
        FilesOperationsRepository repository = p.getOpRepository();
        GZIP.serialize(repository, operationsFile);
    }
}
