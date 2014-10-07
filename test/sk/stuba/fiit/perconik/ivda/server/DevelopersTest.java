package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.junit.Assert;

public class DevelopersTest extends TestCase {

    public void testBlackoutName() throws Exception {
        String name = "Lukas";
        String outcome = Developers.getInstance().blackoutName(name);
        Assert.assertTrue(name.toLowerCase().compareTo(outcome.toLowerCase()) == 0);
    }
}