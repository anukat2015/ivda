package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.util.Set;

/**
 * Otestovanie nacitania white a black listov z konfiguracnej zlozky.
 * Testuju sa prieniky white listov. tzv kazdy proces alebo link ma byt
 * iba v jednom white liste.
 */
public class CatalogTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(CatalogTest.class.getName());

    public void testStaticInit() throws Exception {
        Configuration.getInstance();
        Catalog.staticInit();

        for (Catalog.Processes cat : Catalog.Processes.values()) {
            Assert.assertFalse(cat.getList().getData().isEmpty());
        }
        for (Catalog.Web cat : Catalog.Web.values()) {
            Assert.assertFalse(cat.getList().getData().isEmpty());
        }
    }

    public void testCheckFiles() throws Exception {
        Configuration.getInstance();
        checkFiles();
    }

    public static void checkLists(Catalog.Processes a, Catalog.Processes b) {
        Set<String> same = a.getList().getSameValues(b.getList());
        if (same.isEmpty()) {
            return;
        }
        LOGGER.warn(a.getList().getFileName() + " == " + b.getList().getFileName() + " => ");
        for (String name : same) {
            LOGGER.warn(name);
        }
    }

    public static void checkFiles() {
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.COMMUNICATION);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.NODEVELOPER, Catalog.Processes.TYPICAL);
    }

}
