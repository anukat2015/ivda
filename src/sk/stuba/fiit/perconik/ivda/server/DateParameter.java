package sk.stuba.fiit.perconik.ivda.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Seky on 17. 7. 2014.
 */

public final class DateParameter implements Serializable {
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date date;

    public Date DateParameter(String dateString) {
        try {
            Long time = Long.valueOf(dateString);
            return new Date(time);
        } catch (Exception e) {
            throw new WebApplicationException("Bad date format " + dateString, Response.Status.BAD_REQUEST);
        }
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return df.format(date);
    }
}