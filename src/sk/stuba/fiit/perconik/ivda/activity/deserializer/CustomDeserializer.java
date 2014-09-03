package sk.stuba.fiit.perconik.ivda.activity.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Seky on 20. 7. 2014.
 * <p>
 * Vlastny deserializer. Ktory priradi objekt z jsonu na zaklade specifickeho kluca.
 */
@ThreadSafe
public class CustomDeserializer<T> extends JsonDeserializer<T> {
    private static final Logger LOGGER = Logger.getLogger(CustomDeserializer.class.getName());
    private final Map<String, Class<? extends T>> registry;
    private final String watchedAttribute;

    /**
     * @param attribute Atribut, ktory sa hlada v JSONe.
     */
    public CustomDeserializer(String attribute) {
        watchedAttribute = attribute;
        registry = new ConcurrentHashMap<>(100);
    }

    /**
     * Pomocou tejto metody sa definuju kluce, ktore su priradene triedam.
     *
     * @param key
     * @param aClass
     */
    public void register(String key, Class<? extends T> aClass) {
        key = key.toLowerCase();
        Class<? extends T> previous = registry.put(key, aClass);
        if (previous != null && !previous.equals(aClass)) {
            throw new RuntimeException("Key '" + key + "'already exist as:" + previous + ", new value:" + aClass);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("registry", registry).append("watchedAttribute", watchedAttribute).toString();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    @Override
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode root = mapper.readTree(jp);
        JsonNode attribute = root.get(watchedAttribute);
        if (attribute == null) {
            throw new IOException("Attribute '" + watchedAttribute + "' not found !");
        }
        String key = attribute.asText().toLowerCase();
        Class<? extends T> aClass = searchForClass(key);
        if (aClass == null) {
            throw new IOException("Class not found for '" + key + "'.");
        }
        return mapper.readValue(root.traverse(), aClass);
    }

    protected Class<? extends T> searchForClass(String key) {
        return registry.get(key);
    }

    public String getWatchedAttribute() {
        return watchedAttribute;
    }

}