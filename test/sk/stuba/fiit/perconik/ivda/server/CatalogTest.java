package sk.stuba.fiit.perconik.ivda.server;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.util.Set;

public class CatalogTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(CatalogTest.class.getName());

    public void testStaticInit() throws Exception {
        Catalog.staticInit();

        for (Catalog.Processes cat : Catalog.Processes.values()) {
            Assert.assertTrue(cat.getList().getData().isEmpty());
        }
        for (Catalog.Web cat : Catalog.Web.values()) {
            Assert.assertTrue(cat.getList().getData().isEmpty());
        }
    }

    public void testCheckFiles() throws Exception {
        checkFiles();
    }

    public void checkLists(Catalog.Processes a, Catalog.Processes b) {
        Set<String> same = a.getList().getSameValues(b.getList());
        if (same.isEmpty()) {
            return;
        }
        LOGGER.warn(a.getList().getFileName() + " == " + b.getList().getFileName() + " => ");
        for (String name : same) {
            LOGGER.warn(name);
        }
    }

    public void checkFiles() {
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.COMMUNICATION);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.BANNED, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.NODEVELOPER);
        checkLists(Catalog.Processes.COMMUNICATION, Catalog.Processes.TYPICAL);
        checkLists(Catalog.Processes.NODEVELOPER, Catalog.Processes.TYPICAL);
    }

}