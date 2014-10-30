package sk.stuba.fiit.perconik.ivda.server.servlets;

import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Seky on 23. 10. 2014.
 */
public final class ProcessesRequest {
    private final Date start;
    private final Date end;
    private final String developer;

    public ProcessesRequest(HttpServletRequest req) throws Exception {
        start = DateUtils.fromString(UriUtils.decode(req, "start"));
        end = DateUtils.fromString(UriUtils.decode(req, "end"));
        developer = Developers.getInstance().getRealName(UriUtils.decode(req, "developer"));
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getDeveloper() {
        return developer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProcessesRequest{");
        sb.append("developer=").append(developer);
        sb.append(", end=").append(end);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
    }
}
