package sk.stuba.fiit.perconik.ivda.server.servlets;

import sk.stuba.fiit.perconik.ivda.server.Developers;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Seky on 30. 10. 2014.
 */
public class RestServlet {
    @GET
    @Produces("application/json")
    public List<String> getDevelopers() {
        Set<String> developers = Developers.getInstance().getRealNames();
        List<String> list = new ArrayList<>(developers);
        Collections.sort(list);
        return list;
    }
}
