package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fiit.perconik.ivda.server.Developers;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Seky on 30. 10. 2014.
 */
public class DevelopersServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long serialVersionUID = 6807046140866478480L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Set<String> developers = Developers.getInstance().getRealNames();
            ArrayList<String> list = new ArrayList<>(developers);
            Collections.sort(list);

            resp.setContentType(MediaType.APPLICATION_JSON);
            ServletOutputStream stream = resp.getOutputStream();
            MAPPER.writeValue(stream, list);
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
