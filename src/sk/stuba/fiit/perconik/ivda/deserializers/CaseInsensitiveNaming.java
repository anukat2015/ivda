package sk.stuba.fiit.perconik.ivda.deserializers;

import org.codehaus.jackson.map.PropertyNamingStrategy;

/**
 * Created by Seky on 20. 7. 2014.
 */
public class CaseInsensitiveNaming extends PropertyNamingStrategy.PropertyNamingStrategyBase {
    @Override
    public String translate(String propertyName) {
        return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
}
