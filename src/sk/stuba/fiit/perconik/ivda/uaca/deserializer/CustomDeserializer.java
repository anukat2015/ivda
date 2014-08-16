package sk.stuba.fiit.perconik.ivda.uaca.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 20. 7. 2014.
 * <p/>
 * Vlastny deserializer. Ktory priradi objekt z jsonu na zaklade specifickeho kluca.
 */
public class CustomDeserializer<T> extends JsonDeserializer<T> {
    protected final Map<String, Class<? extends T>> registry;
    private final String watchedAttribute;

    /**
     * @param attribute Atribut, ktory sa hlada v JSONe.
     */
    public CustomDeserializer(String attribute) {
        watchedAttribute = attribute;
        registry = new HashMap<>(100);
    }

    /**
     * Pomocou tejto metody sa definuju kluce, ktore su priradene triedam.
     *
     * @param key
     * @param aClass
     */
    public void register(String key, Class<? extends T> aClass) {
        if (registry.containsKey(key)) {
            throw new RuntimeException("Key '" + key + "'already exist");
        }
        registry.put(key, aClass);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("registry", registry).append("watchedAttribute", watchedAttribute).toString();
    }

    @Override
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode root = mapper.readTree(jp);
        JsonNode attribute = root.get(watchedAttribute);
        if (attribute == null) {
            throw new IOException("Attribute '" + watchedAttribute + "' not found !");
        }
        Class<? extends T> aClass = searchForClass(attribute.asText());
        if (aClass == null) {
            throw new IOException("Class not found for '" + attribute.asText() + "'.");
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