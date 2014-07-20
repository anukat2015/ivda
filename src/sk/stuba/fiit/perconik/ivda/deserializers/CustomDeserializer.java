package sk.stuba.fiit.perconik.ivda.deserializers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 20. 7. 2014.
 */
public class CustomDeserializer<T> extends JsonDeserializer<T> {
    private Map<String, Class<? extends T>> registry;
    private String watchedAttribute;

    public CustomDeserializer(String attribute) {
        watchedAttribute = attribute;
        registry = new HashMap<>();
    }

    public void register(String str,
                         Class<? extends T> aClass) {
        registry.put(str, aClass);
    }

    @Override
    public T deserialize(
            JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode root = mapper.readTree(jp);
        JsonNode attribute = root.get(watchedAttribute);
        if (attribute == null) {
            throw new IOException("Attribute '" + attribute + "' not found !");
        }
        Class<? extends T> aClass = registry.get(attribute.getTextValue());
        if (aClass == null) {
            throw new IOException("Class not found for '" + attribute.getTextValue() + "'.");
        }
        return mapper.readValue(root, aClass);
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