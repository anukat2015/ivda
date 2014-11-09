package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.processes.PerUserProcesses;
import sk.stuba.fiit.perconik.ivda.server.processes.Process;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 22. 10. 2014.
 * <p/>
 * Servlet pre vypisanie procesov pre daneho pouzivatel v danom case.
 */
public class ProcessesServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProcessesServlet.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ImmutableMap<String, PerUserProcesses> PROCESSES;

    static {
        Configuration.getInstance();
        File processesFile = new File(Configuration.CONFIG_DIR, "processes.gzip");
        try {
            PROCESSES = (ImmutableMap<String, PerUserProcesses>) GZIP.deserialize(processesFile);
        } catch (Exception e) {
            throw new RuntimeException(processesFile + " file is missing.");
        }
    }

    private static final long serialVersionUID = -6630885232172276063L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ProcessesRequest request = new ProcessesRequest(req);
            LOGGER.info("Request: " + request);
            ImmutableList<Process> list = findProcesses(request);
            resp.setContentType(MediaType.APPLICATION_JSON);
            ServletOutputStream stream = resp.getOutputStream();
            MAPPER.writeValue(stream, list);
        } catch (Exception e) {
            LOGGER.error("Reponse: ", e);
            resp.sendError(500, e.getMessage());
        }
    }

    private static ImmutableList<Process> findProcesses(ProcessesRequest req) {
        List<Process> saved = new ArrayList<>(128);
        PerUserProcesses info = PROCESSES.get(req.getDeveloper());
        if (info != null) {
            for (Process p : info.getFinished()) {
                if (p.isOverlaping(req.getStart(), req.getEnd())) {
                    saved.add(p);
                }
            }
        }

        fixOverLapingProccesses(saved);
        return ImmutableList.copyOf(saved);
    }

    private static void fixOverLapingProccesses(List<Process> list) {
        for (Process p1 : list) {
            Integer number = 1;
            for (Process p2 : list) {
                if (!p1.equals(p2) && p1.getName().equals(p2.getName()) && p1.isOverlaping(p2)) {
                    p2.setName(p2.getName() + number);
                    number++;
                }
            }
        }
    }
}
