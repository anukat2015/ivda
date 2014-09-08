package sk.stuba.fiit.perconik.ivda.util;

import com.google.common.base.Preconditions;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 23. 7. 2014.
 * <p>
 * Pomocna trieda na formatovanie datumov.
 * Javascript posiela ISO 8601, sluzba si to pyta.
 * Vytvorene na zaklade:
 * http://stackoverflow.com/questions/17319793/convert-date-or-calendar-type-into-string-FORMAT
 * http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
 * Oni to prerobili zase na iny FORMAT, ach !
 */
public final class DateUtils {
    private static final ThreadLocal<DateFormat> FORMATTER = new ThreadLocal<DateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); //new ISO8601DateFormat();
        }
    };

    public static GregorianCalendar fromString(String dateString) throws ParseException {
        Preconditions.checkNotNull(dateString);
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        Date datum = FORMATTER.get().parse(dateString);
        gc.setTimeInMillis(datum.getTime());
        gc.setLenient(false);
        return gc;
    }

    public static String toString(GregorianCalendar calender) {
        return FORMATTER.get().format(calender.getTime());
    }

    public static GregorianCalendar getNow() {
        return new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    }

    public static long diff(GregorianCalendar actual, GregorianCalendar last) {
        return actual.getTimeInMillis() - last.getTimeInMillis();
    }

    public static boolean diff(GregorianCalendar actual, GregorianCalendar last, TimeUnit unit) {
        return diff(actual, last) == unit.toMicros(1);
    }

    public static boolean isRounded(GregorianCalendar actual, TimeUnit unit) {
        return actual.getTimeInMillis() % unit.toMicros(1) == 0;
    }

}
