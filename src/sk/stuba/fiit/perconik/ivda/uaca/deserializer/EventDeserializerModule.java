package sk.stuba.fiit.perconik.ivda.uaca.deserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by Seky on 20. 7. 2014.
 */
public final class EventDeserializerModule extends SimpleModule {
    private PolymorphicDeserializer<EventDto> deserializer;

    abstract class XMLGregorianCalendarMixIn {
        @JsonIgnore
        public abstract void setYear(int year);
    }

    public EventDeserializerModule() {
        super("PolymorphicAnimalDeserializerModule", new Version(1, 0, 0, "1.0-SNAPSHOT", "sk.stuba.fiit.perconik.ivda", "sk.stuba.fiit.perconik.ivda"));

        deserializer = new PolymorphicDeserializer<>(EventDto.class, "eventTypeUri");
        deserializer.pushSubTypesOf("sk.stuba.fiit.perconik.ivda.uaca.dto");
        addDeserializer(EventDto.class, deserializer);
        setMixInAnnotation(XMLGregorianCalendar.class, XMLGregorianCalendarMixIn.class);
    }
}
