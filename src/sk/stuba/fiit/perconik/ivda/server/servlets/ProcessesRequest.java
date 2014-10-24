package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.google.common.base.Splitter;
import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Seky on 23. 10. 2014.
 */
public final class ProcessesRequest {
    private final Date start;
    private final Date end;
    private final List<String> developers;

    public ProcessesRequest(HttpServletRequest req) throws Exception {
        start = DateUtils.fromString(UriUtils.decode(req, "start"));
        end = DateUtils.fromString(UriUtils.decode(req, "end"));

        // Spracuj developerov
        List<String> parsedUsers = Splitter.on(',').splitToList(UriUtils.decode(req, "developers"));
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
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessesRequest{");
        sb.append("developers=").append(developers);
        sb.append(", end=").append(end);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
    }
}
