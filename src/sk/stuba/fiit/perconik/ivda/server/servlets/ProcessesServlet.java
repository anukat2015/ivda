package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.server.processes.Process;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.GZIP;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Seky on 22. 10. 2014.
 * <p/>
 * Servlet pre vypisanie procesov pre daneho pouzivatel v danom case.
 */
public class ProcessesServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProcessesServlet.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ImmutableList<Process> processes;

    static {
        Configuration.getInstance();
        File processesFile = new File("C:\\processes.gzip");
        try {
            processes = (ImmutableList<Process>) GZIP.deserialize(processesFile);
        } catch (Exception e) {
            throw new RuntimeException(processesFile + " file is missing.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Date start = DateUtils.fromString(UriUtils.decode(req, "start"));
            Date end = DateUtils.fromString(UriUtils.decode(req, "end"));
            String author = null;

            ImmutableList<Process> list = findProcesses(start, end, author);
            resp.setContentType(MediaType.APPLICATION_JSON);
            ServletOutputStream stream = resp.getOutputStream();
            MAPPER.writeValue(stream, list);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    private static ImmutableList<Process> findProcesses(Date start, Date end, String name) {
        List<Process> saved = new ArrayList<>();
        for (Process p : processes) {
            if (p.isOverlaping(start, end)) {
                saved.add(p);
            }
        }
        fixOverLapingProccesses(saved);
        return ImmutableList.copyOf(saved);
    }

    private static void fixOverLapingProccesses(List<Process> list) {
        for (Process a : list) {
            Integer number = 1;
            for (Process b : list) {
                if (a != b && a.getName().equals(b.getName()) && a.isOverlaping(b)) {
                    b.setName(b.getName() + number);
                    number++;
                }
            }
        }
    }
}
