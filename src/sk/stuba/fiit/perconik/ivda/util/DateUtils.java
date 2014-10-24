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
 * <p/>
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

    public static Date fromString(String dateString) throws ParseException {
        Preconditions.checkNotNull(dateString);
        return FORMATTER.get().parse(dateString);
    }

    public static String toString(Date calender) {
        return FORMATTER.get().format(calender.getTime());
    }

    public static Date getNow() {
        return new GregorianCalendar(TimeZone.getTimeZone("GMT")).getTime();
    }

    public static long diff(Date actual, Date last) {
        return last.getTime() - actual.getTime();
    }

    public static boolean isDiff(Date actual, Date last, TimeUnit unit) {
        return diff(actual, last) == unit.toMillis(1);
    }

    public static boolean isRounded(Date actual, TimeUnit unit) {
        return actual.getTime() % unit.toMillis(1) == 0;
    }

    public static boolean isOverlaping(Date s1, Date e1, Date start2, Date end2) {
        return e1.after(start2) && s1.before(end2);
    }
}
