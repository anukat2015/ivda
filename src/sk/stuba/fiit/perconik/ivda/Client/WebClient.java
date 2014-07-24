package sk.stuba.fiit.perconik.ivda.Client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.deserializer.JacksonContextResolver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by Seky on 18. 7. 2014.
 */
public class WebClient {
    private static final Logger logger = Logger.getLogger(WebClient.class.getName());

    private Client client;

    public WebClient() {
        client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).register(JacksonContextResolver.class).build();
    }

    public Object synchronizedRequest(URI uri, Class<?> aClass) {
        WebTarget fullTarget = client.target(uri);
        Invocation invocation = fullTarget.request(MediaType.APPLICATION_JSON_TYPE).buildGet();
        Response response = invocation.invoke();

        try {
            Response.Status.Family status = response.getStatusInfo().getFamily();
            if (status != Response.Status.Family.SUCCESSFUL) {
                logger.error("WebClient error");
            }
            Object object = response.readEntity(aClass);
            //logger.info("Downloaded:");
            //logger.info(object);
            return object;
        } finally {
            response.close();
        }
    }
}