package sk.stuba.fiit.perconik.ivda.server;

import com.gratex.perconik.useractivity.app.dto.EventDto;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import sk.stuba.fiit.perconik.ivda.Configuration;
import sk.stuba.fiit.perconik.ivda.deserializers.JacksonContextResolver;
import sk.stuba.fiit.perconik.ivda.dto.PagedResponse;
import sk.stuba.fiit.perconik.ivda.dto.SearchResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Seky on 18. 7. 2014.
 */
public class UacaClient {
    private static final Logger logger = Logger.getLogger(UacaClient.class.getName());

    private Client client;
    private URI destination;

    public UacaClient() {
        client = ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .register(JacksonContextResolver.class)
                .build();

        String url = Configuration.getInstance().getUacaLink();
        try {
            destination = new URI(url);
        } catch (URISyntaxException e) {
            logger.error("Configuration UACA url malformed.");
            throw new RuntimeException("Configuration UACA url malformed.");
        }
    }

    private Object synchronizedRequest(URI uri, Class<?> aClass) {
        WebTarget fullTarget = client.target(uri);
        Invocation invocation = fullTarget.request(MediaType.APPLICATION_JSON_TYPE).buildGet();
        Response response = invocation.invoke();

        try {
            Response.Status.Family status = response.getStatusInfo().getFamily();
            if (status != Response.Status.Family.SUCCESSFUL) {
                logger.error("UacaClient error");
            }
            return response.readEntity(aClass);
        } finally {
            response.close();
        }
    }

    public UriBuilder getDestination() {
        return UriBuilder.fromUri(destination);
    }

    public PagedResponse<EventDto> getUserActivity() {
        UriBuilder builder = getDestination();
        builder.path("useractivity");
        builder.queryParam("timefrom", "2014-04-16T12:00Z");
        builder.queryParam("page", 1);
        builder.queryParam("pagesize", 10);
        return (PagedResponse<EventDto>) synchronizedRequest(builder.build(), SearchResponse.class);
    }

    public static void main(String[] args) throws Exception {
        UacaClient client = new UacaClient();
        PagedResponse response = client.getUserActivity();
        logger.info(response);

    }
}

/*AstRcsWcfSvc ast = new AstRcsWcfSvc();
IAstRcsWcfSvc service = ast.getBasicHttpBindingIAstRcsWcfSvc();

SearchUsersRequest r = new SearchUsersRequest();
r.setLogin( new JAXBElement<String>("a") );
service.searchUsers(r);
System.out.println(hello.getHelloWorldAsString("mkyong"));
*/