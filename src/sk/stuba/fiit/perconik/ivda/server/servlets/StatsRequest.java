package sk.stuba.fiit.perconik.ivda.server.servlets;

import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.lang.TimeGranularity;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Seky on 9. 11. 2014.
 */
public final class StatsRequest {
    private final Date start;
    private final Date end;
    private final String developer;
    private final String attribute;
    private final TimeGranularity granularity;

    public StatsRequest(HttpServletRequest req) throws Exception {
        start = DateUtils.fromString(UriUtils.decode(req, "start"));
        end = DateUtils.fromString(UriUtils.decode(req, "end"));
        developer = Developers.getInstance().getRealName(UriUtils.decode(req, "developer"));
        attribute = UriUtils.decode(req, "attribute");
        granularity = TimeGranularity.valueOf(UriUtils.decode(req, "granularity"));
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

    public String getAttribute() {
        return attribute;
    }

    public TimeGranularity getGranularity() {
        return granularity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatsRequest{");
        sb.append("attribute='").append(attribute).append('\'');
        sb.append(", developer='").append(developer).append('\'');
        sb.append(", end=").append(end);
        sb.append(", granularity=").append(granularity);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
    }
}
