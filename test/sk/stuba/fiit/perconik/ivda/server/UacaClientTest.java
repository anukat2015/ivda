package sk.stuba.fiit.perconik.ivda.server;

import org.apache.log4j.Logger;
import org.junit.Test;
import sk.stuba.fiit.perconik.ivda.entities.PagedResponse;

public class UacaClientTest {
    private static final Logger logger = Logger.getLogger(UacaClientTest.class.getName());

    @Test
    public void testGetUserActivity() throws Exception {
        UacaClient client = new UacaClient();
        PagedResponse response = client.getUserActivity();
        logger.info(response);
    }
}