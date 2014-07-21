package sk.stuba.fiit.perconik.ivda.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 20. 7. 2014.
 */
public class CustomDeserializer<T> extends JsonDeserializer<T> {
    protected Map<String, Class<? extends T>> registry;
    private String watchedAttribute;

    public CustomDeserializer(String attribute) {
        watchedAttribute = attribute;
        registry = new HashMap<>();
    }

    public void register(String key, Class<? extends T> aClass) {
        if (registry.containsKey(key)) {
            throw new RuntimeException("Key '" + key + "'already exist");
        }
        registry.put(key, aClass);
    }

    @Override
    public T deserialize(
            JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode root = mapper.readTree(jp);
        JsonNode attribute = root.get(watchedAttribute);
        if (attribute == null) {
            throw new IOException("Attribute '" + attribute + "' not found !");
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

    @Override
    public String toString() {
        return "CustomDeserializer{" +
                "registry=" + registry +
                ", watchedAttribute='" + watchedAttribute + '\'' +
                '}';
    }
}