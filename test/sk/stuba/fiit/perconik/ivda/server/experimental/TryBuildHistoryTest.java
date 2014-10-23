package sk.stuba.fiit.perconik.ivda.server.experimental;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

public class TryBuildHistoryTest extends TestCase {

    public void testBuild() throws Exception {
        Configuration.getInstance();
        TryBuildHistory h = new TryBuildHistory();
        h.build();
    }
}
