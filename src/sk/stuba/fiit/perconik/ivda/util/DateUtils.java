package sk.stuba.fiit.perconik.ivda.util;

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Seky on 23. 7. 2014.
 * <p/>
 * Pomocna trieda na formatovanie datumov.
 * Javascript posiela ISO 8601, sluzba si to pyta.
 * Vytvorene na zaklade:
 * http://stackoverflow.com/questions/17319793/convert-date-or-calendar-type-into-string-format
 * http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
 * Oni to prerobili zase na iny format, ach !
 */
public final class DateUtils {
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); //new ISO8601DateFormat();
    private static final TimeZone tz = TimeZone.getTimeZone("GMT");

    public static GregorianCalendar fromString(String dateString) {
        GregorianCalendar gc = new GregorianCalendar(tz);
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

}
