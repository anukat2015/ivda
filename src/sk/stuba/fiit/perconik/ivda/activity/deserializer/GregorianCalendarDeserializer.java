package sk.stuba.fiit.perconik.ivda.activity.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Seky on 9. 8. 2014.
 * <p/>
 * Deserializer pre triedu GregorianCalendar.
 */
public final class GregorianCalendarDeserializer extends JsonDeserializer<GregorianCalendar> {
    @Override
    public GregorianCalendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String text = jp.getText();
        if (text == null || text.isEmpty()) {
            throw new IOException("dateString is empty");
        }
        String tzPart = text.substring(text.length() - 5, text.length());
        if (!"0000Z".equals(tzPart)) {
            throw new RuntimeException("Zase zmenili format datumu ....");
        }
        try {
            return DateUtils.fromString(text.substring(0, text.length() - 5));
        } catch (ParseException e) {
            throw new IOException("dateString nemozem precitat.", e);
        }
    }
}
