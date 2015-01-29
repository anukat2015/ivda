package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.servlets.IvdaEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Metoda spracovania prvkov, ktora je rozsirena o moznost ukladat zaujimave udalosti.
 */
public final class Array2Json {
    private static final Logger LOGGER = Logger.getLogger(Array2Json.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private JsonGenerator generator;

    public Array2Json(OutputStream out) {
        try {
            generator = MAPPER.getFactory().createGenerator(out, JsonEncoding.UTF8);
        } catch (IOException e) {
            LOGGER.error("error, ", e);
        }
    }

    public void start() {
        if (generator == null) {
            return;
        }
        try {
            // Zapisame az ked musime
            generator.writeStartArray();
        } catch (IOException e) {
            LOGGER.error("error, ", e);
        }
    }

    public void close() {
        if (generator == null) {
            return;
        }
        try {
            generator.writeEndArray();
            generator.flush();
            generator.close();
        } catch (IOException e) {
            LOGGER.error("error, ", e);
        }
    }

    public void write(IvdaEvent event) {
        try {
            generator.writeObject(event);
        } catch (IOException e) {
            LOGGER.error("error, ", e);
        }
    }

    public void write(EventDto e, @Nullable String content, @Nullable Integer value, @Nullable Object metadata) {
        String group = EventsUtil.event2name(e);
        IvdaEvent event = new IvdaEvent();
        event.setId(e.getEventId());
        event.setStart(e.getTimestamp());
        event.setContent(content != null ? content : group);
        event.setGroup(group);
        event.setY(value);
        event.setMetadata(metadata);
        write(event);
    }
}

