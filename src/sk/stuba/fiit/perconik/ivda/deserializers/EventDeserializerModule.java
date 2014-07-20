package sk.stuba.fiit.perconik.ivda.deserializers;

import com.gratex.perconik.useractivity.app.dto.EventDto;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * Created by Seky on 20. 7. 2014.
 */
public final class EventDeserializerModule extends SimpleModule {
    private PolymorhicDeserializer<EventDto> deserializer;

    public EventDeserializerModule() {
        super("PolymorphicAnimalDeserializerModule",
                new Version(1, 0, 0, null));

        deserializer = new PolymorhicDeserializer<>(EventDto.class, "eventTypeUri");
        deserializer.pushSubTypesOf("com.gratex.perconik.useractivity.app.dto");
        addDeserializer(EventDto.class, deserializer);
    }
}
