package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.google.common.base.Splitter;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 6. 9. 2014.
 */
public final class TimelineRequest {
    //private final Integer width;
    //private final Integer step;
    //private final Integer scale;
    private static final TimeUnit SIZE_OF_CHUNK = TimeUnit.HOURS;
    private final GregorianCalendar start;
    private final GregorianCalendar end;
    private final List<String> developers;

    public TimelineRequest(HttpServletRequest req) throws Exception {
        // Vplyv na rozsah ma jedine zoom, ize musime vypocitat sirku okna a poslat to sem
        start = DateUtils.fromString(req.getParameter("start"));
        end = DateUtils.fromString(req.getParameter("end"));
        //width = Integer.valueOf(req.getParameter("width"));
        //step = Integer.valueOf(req.getParameter("step"));
        //scale = Integer.valueOf(req.getParameter("scale"));

        // Spracuj developerov
        List<String> parsedUsers = Splitter.on(',').splitToList(req.getParameter("developers"));
        ArrayList<String> users = new ArrayList<>();
        for (String user : parsedUsers) {
            String realName = Developers.getRealName(user);
            if (realName != null) {
                users.add(realName);
            }
        }
        developers = Collections.unmodifiableList(users);

        // Skontroluj datumy
        if (!DateUtils.isRounded(start, SIZE_OF_CHUNK)) {
            throw new WebApplicationException("Start datum nie je zaokruhleny na velkost chunku.", Response.Status.BAD_REQUEST);
        }
        if (!DateUtils.isRounded(end, SIZE_OF_CHUNK)) {
            throw new WebApplicationException("End datum nie je zaokruhleny na velkost chunku.", Response.Status.BAD_REQUEST);
        }
        if (!DateUtils.diff(start, end, TimeUnit.HOURS)) {
            throw new WebApplicationException("Rozdiel datumov je vacsi ako velkost chunku.", Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("developers", developers).append("end", end).append("start", start).toString();
    }

    public GregorianCalendar getStart() {
        return start;
    }

    public GregorianCalendar getEnd() {
        return end;
    }

    public long diffTime() {
        return end.getTimeInMillis() - start.getTimeInMillis();
    }

    public boolean containDeveloper(String name) {
        for (String developer : developers) {
            if (name.equals(developer)) {
                return true;
            }
        }
        return false;
    }
}
