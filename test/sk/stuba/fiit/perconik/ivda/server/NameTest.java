package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

public class NameTest extends TestCase {

    public void testBlackoutName() throws Exception {
        Configuration.getInstance();
        String name = "Lukas";
        String outcome = Developers.getInstance().blackoutName(name);
        Assert.assertTrue(name.toLowerCase().compareTo(outcome.toLowerCase()) == 0);
    }
}
