package sk.stuba.fiit.perconik.ivda.util.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.deserializer.JacksonContextResolver;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by Seky on 18. 7. 2014.
 * <p>
 * HTTP jednoduchy client na stahovanie odpovedi zo sluzieb.
 */
@ThreadSafe
public class WebClient {
    private static final Logger LOGGER = Logger.getLogger(WebClient.class.getName());
    private final Client client;

    public WebClient() {
        // Zaregistruj nase providery
        client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).register(JacksonContextResolver.class).build();
    }

    public void close() {
        client.close();
    }

    public final Object synchronizedRequest(URI uri, Class<?> aClass) {
        WebTarget fullTarget = client.target(uri);


        Invocation invocation = fullTarget.request(MediaType.APPLICATION_JSON_TYPE).buildGet();
        LOGGER.info("synchronizedRequest " + uri);
        Response response = invocation.invoke();

        try {
            Response.Status.Family status = response.getStatusInfo().getFamily();
            if (status != Response.Status.Family.SUCCESSFUL) {
                LOGGER.error("WebClient error");
            }
            @SuppressWarnings("UnnecessaryLocalVariable")
            Object object = response.readEntity(aClass);
            //LOGGER.info("Downloaded:");
            //LOGGER.info(object);
            return object;
        } finally {
            response.close();
        }
    }
}