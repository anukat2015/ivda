package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.google.common.base.Splitter;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private final Date start;
    private final Date end;
    private final List<String> developers;

    public TimelineRequest(HttpServletRequest req) throws Exception {
        // Vplyv na rozsah ma jedine zoom, ize musime vypocitat sirku okna a poslat to sem
        start = DateUtils.fromString(decode(req, "start"));
        end = DateUtils.fromString(decode(req, "end"));
        //width = Integer.valueOf(req.getParameter("width"));
        //step = Integer.valueOf(req.getParameter("step"));
        //scale = Integer.valueOf(req.getParameter("scale"));

        // Spracuj developerov
        List<String> parsedUsers = Splitter.on(',').splitToList(decode(req, "developers"));
        if (parsedUsers.isEmpty()) {
            throw new Exception("Specifikuj aspon jedneho vyvojara.");
        }
        ArrayList<String> users = new ArrayList<>();
        for (String user : parsedUsers) {
            String realName = Developers.getInstance().getRealName(user);
            if (realName != null) {
                users.add(realName);
            }
        }
        developers = Collections.unmodifiableList(users);

        // Skontroluj datumy
        if (!DateUtils.isRounded(start, SIZE_OF_CHUNK)) {
            throw new Exception("Start datum nie je zaokruhleny na velkost chunku.");
        }
        if (!DateUtils.isRounded(end, SIZE_OF_CHUNK)) {
            throw new Exception("End datum nie je zaokruhleny na velkost chunku.");
        }
        if (!DateUtils.diff(start, end, TimeUnit.HOURS)) {
            throw new Exception("Rozdiel datumov je vacsi ako velkost chunku.");
        }
    }

    private static String decode(HttpServletRequest req, String key) throws Exception {
        return URLDecoder.decode(req.getParameter(key), "UTF-8");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("developers", developers).append("end", end).append("start", start).toString();
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public long diffTime() {
        return DateUtils.diff(start, end);
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
