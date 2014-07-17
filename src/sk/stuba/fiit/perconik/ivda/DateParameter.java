package sk.stuba.fiit.perconik.ivda;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Seky on 17. 7. 2014.
 */

public class DateParameter implements Serializable {
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Date date;

    public DateParameter(String dateString) {
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            throw new WebApplicationException("Date format should be " + df.toString(), Response.Status.BAD_REQUEST);
        }
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date.toString();
    }
}