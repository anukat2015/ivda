package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;

public class TryBuildHistoryTest extends TestCase {

    public void testBuild() throws Exception {
        TryBuildHistory h = new TryBuildHistory();
        h.build();
    }
}