package sk.stuba.fiit.perconik.ivda;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * Created by Seky on 23. 7. 2014.
 */
public class DateUtils {
    // Javascript posiela ISO 8601, sluzba si to pyta
    // SimpleDateFormat nepodporuje 'XXX' premenne pri parsovani, podla dokumentacie ale ANO
    // http://stackoverflow.com/questions/17319793/convert-date-or-calendar-type-into-string-format
    // http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
    private static final DateFormat format = new ISO8601DateFormat();

    static {

    }

    public static GregorianCalendar fromString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("dateString is empty");
        }
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("Europe/Bratislava"));
        try {
            gc.setTime(format.parse(dateString));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        return gc;
    }

    public static String toString(GregorianCalendar c) {
        return format.format(c.getTime());
    }

    public static GregorianCalendar createUtcNow() {
        return new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    }
}
