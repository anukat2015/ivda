package sk.stuba.fiit.perconik.ivda.server.experimental;

import junit.framework.TestCase;
import sk.stuba.fiit.perconik.ivda.server.experimental.TryBuildHistory;

public class TryBuildHistoryTest extends TestCase {

    public void testBuild() throws Exception {
        TryBuildHistory h = new TryBuildHistory();
        h.build();
    }
}
