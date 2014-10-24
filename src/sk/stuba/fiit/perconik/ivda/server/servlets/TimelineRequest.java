package sk.stuba.fiit.perconik.ivda.server.servlets;

import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 6. 9. 2014.
 */
public final class TimelineRequest {
    private static final TimeUnit SIZE_OF_CHUNK = TimeUnit.DAYS;
    private final Date start;
    private final Date end;

    public TimelineRequest(HttpServletRequest req) throws Exception {
        // Vplyv na rozsah ma jedine zoom, ize musime vypocitat sirku okna a poslat to sem
        start = DateUtils.fromString(UriUtils.decode(req, "start"));
        end = DateUtils.fromString(UriUtils.decode(req, "end"));

        // Skontroluj datumy
        if (!DateUtils.isRounded(start, SIZE_OF_CHUNK)) {
            throw new Exception("Start datum nie je zaokruhleny na velkost chunku.");
        }
        if (!DateUtils.isRounded(end, SIZE_OF_CHUNK)) {
            throw new Exception("End datum nie je zaokruhleny na velkost chunku.");
        }
        if (!DateUtils.isDiff(start, end, SIZE_OF_CHUNK)) {
            throw new Exception("Rozdiel datumov je vacsi ako velkost chunku.");
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("end", end).append("start", start).toString();
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
}
