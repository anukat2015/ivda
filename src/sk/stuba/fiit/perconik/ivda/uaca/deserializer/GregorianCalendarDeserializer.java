package sk.stuba.fiit.perconik.ivda.uaca.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ibm.icu.util.GregorianCalendar;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.io.IOException;

/**
 * Created by Seky on 9. 8. 2014.
 */
public final class GregorianCalendarDeserializer extends JsonDeserializer<GregorianCalendar> {
    @Override
    public GregorianCalendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String text = jp.getText();
        if (text == null || text.isEmpty()) {
            throw new IOException("dateString is empty");
        }
        String tzPart = text.substring(text.length() - 5, text.length());
        if (!tzPart.equals("0000Z")) {
            throw new IOException("Zase zmenili format datumu ....");
        }
        return DateUtils.fromString(text.substring(0, text.length() - 5));
    }
}
