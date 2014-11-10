package sk.stuba.fiit.perconik.ivda.server.servlets;

import sk.stuba.fiit.perconik.ivda.server.Developers;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 6. 9. 2014.
 */
public final class IvdaRequest {
    private static final TimeUnit SIZE_OF_CHUNK = TimeUnit.DAYS;
    private final Date start;
    private final Date end;
    private final String developer;

    public IvdaRequest(HttpServletRequest req) throws Exception {
        // Vplyv na rozsah ma jedine zoom, ize musime vypocitat sirku okna a poslat to sem
        Date s = DateUtils.fromString(UriUtils.decode(req, "start"));
        Date e = DateUtils.fromString(UriUtils.decode(req, "end"));
        developer = Developers.getInstance().getRealName(UriUtils.decode(req, "developer"));

        start = org.apache.commons.lang.time.DateUtils.truncate(s, Calendar.DAY_OF_MONTH);
        end = org.apache.commons.lang.time.DateUtils.truncate(e, Calendar.DAY_OF_MONTH);

        // Skontroluj datumy
        if (!DateUtils.isDiff(start, end, SIZE_OF_CHUNK)) {
            throw new Exception("Rozdiel datumov je vacsi ako velkost chunku.");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimelineRequest{");
        sb.append("developer='").append(developer).append('\'');
        sb.append(", end=").append(end);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
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

    public long diffTime() {
        return DateUtils.diff(start, end);
    }
}
