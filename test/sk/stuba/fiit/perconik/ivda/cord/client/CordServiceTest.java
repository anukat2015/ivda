package sk.stuba.fiit.perconik.ivda.cord.client;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class CordServiceTest extends TestCase {
    protected static final Logger LOGGER = Logger.getLogger(CordServiceTest.class.getName());
    private static final String ZAUJIMAVY_SUBOR = "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java";


    public void testGetRepositories() throws Exception {
        LOGGER.info(CordService.getInstance().getRepositories(""));
    }
}