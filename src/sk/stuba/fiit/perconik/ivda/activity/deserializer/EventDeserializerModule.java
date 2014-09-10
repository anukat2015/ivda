package sk.stuba.fiit.perconik.ivda.activity.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;

/**
 * Created by Seky on 20. 7. 2014.
 * <p/>
 * Pomocou nasho modulu povieme Jacksonu aby pouzil nas vlastny deserializer.
 */
public final class EventDeserializerModule extends SimpleModule {
    private static final Logger LOGGER = Logger.getLogger(EventDeserializerModule.class.getName());
    private static final String ENTITIES_PACKAGE = "sk.stuba.fiit.perconik.ivda.activity.dto";
    private static final String GROUP_ID = "sk.stuba.fiit.perconik.ivda"; // alias adresa projektu
    private static final long serialVersionUID = -4285741198907663688L;

    public EventDeserializerModule() {
        super("PolymorphicDeserializerModule", new Version(1, 0, 0, "1.0-SNAPSHOT", GROUP_ID, GROUP_ID));
        PolymorphicDeserializer<EventDto> deserializer = new PolymorphicDeserializer<>(EventDto.class, "eventTypeUri");
        deserializer.pushSubTypesOf(ENTITIES_PACKAGE);
        addDeserializer(EventDto.class, deserializer);
        LOGGER.debug("PolymorphicDeserializerModule loaded");
    }
}
