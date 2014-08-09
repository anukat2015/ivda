package sk.stuba.fiit.perconik.ivda.util;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Created by Seky on 23. 7. 2014.
 * <p/>
 * Pomocna trieda na formatovanie datumov.
 * Javascript posiela ISO 8601, sluzba si to pyta.
 * Vytvorene na zaklade:
 * http://stackoverflow.com/questions/17319793/convert-date-or-calendar-type-into-string-format
 * http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
 */
public final class DateUtils {
    private static final DateFormat format = new ISO8601DateFormat();
    private static final TimeZone tz = TimeZone.getTimeZone("Europe/Bratislava");

    public static GregorianCalendar fromString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("dateString is empty");
        }
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

    public static GregorianCalendar createUtcNow() {
        return new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    }

    public static GregorianCalendar normalizeGregorianCalendar(XMLGregorianCalendar c) {
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(c.toGregorianCalendar().getTime());
        return timestamp;
    }
}
