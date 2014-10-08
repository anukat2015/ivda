package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Otestovanie vypoctu metrik nad kodom.
 */
public class EventsUtilTest extends TestCase {

    public void testCodeWritten() throws Exception {
        int lines;
        lines = EventsUtil.codeWritten("  ass\ndas");
        Assert.assertTrue(lines == 2);

        lines = EventsUtil.codeWritten("  assdas");
        Assert.assertTrue(lines == 1);

        lines = EventsUtil.codeWritten("");
        Assert.assertTrue(lines == 0);

        lines = EventsUtil.codeWritten("     ");
        Assert.assertTrue(lines == 0);

        lines = EventsUtil.codeWritten("\t\n\t");
        Assert.assertTrue(lines == 0);
    }
}