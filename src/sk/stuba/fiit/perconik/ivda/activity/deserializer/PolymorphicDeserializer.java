package sk.stuba.fiit.perconik.ivda.activity.deserializer;

import com.google.common.base.Function;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import sk.stuba.fiit.perconik.ivda.util.Objects;
import sk.stuba.fiit.perconik.ivda.util.Strings;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Seky on 20. 7. 2014.
 * <p/>
 * Deserialize polymorfed objects by specific keys. Like a URI
 */
@ThreadSafe
public class PolymorphicDeserializer<T> extends CustomDeserializer<T> {
    private static final Logger LOGGER = Logger.getLogger(PolymorphicDeserializer.class.getName());

    private final Class<T> baseClass;
    private final boolean mTryLongestSubsequence;

    /**
     * When system can not find class for http://perconik.gratex.com/useractivity/event/web/tab/switchto
     * it will try search for nearest object like a http://perconik.gratex.com/useractivity/event/web/tab
     * and it will map for this class.
     *
     * @param aClass                Definition of class
     * @param jsonAttribute         Name of jason atribute
     * @param tryLongestSubsequence on / off looking for subsequnce
     */
    public PolymorphicDeserializer(Class<T> aClass, String jsonAttribute, boolean tryLongestSubsequence) {
        super(jsonAttribute);
        baseClass = aClass;
        mTryLongestSubsequence = tryLongestSubsequence;
    }

    public PolymorphicDeserializer(Class<T> baseClass, String attribute) {
        this(baseClass, attribute, true);
    }

    /**
     * Call object's method to get specific object's value and compare it with JSON attribute
     *
     * @param packageName
     * @param callMethod
     */
    public void pushSubTypesOf(String packageName, Method callMethod) throws Exception {
        LOGGER.debug("Package exploring: " + packageName);
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(baseClass);
        if (subTypes.isEmpty()) {
            LOGGER.warn("Package '" + packageName + "' is empty.");
        }

        for (Class<? extends T> aClass : subTypes) {
            @SuppressWarnings("ClassNewInstance")
            Object object = callMethod.invoke(aClass.newInstance());
            register(object.toString(), aClass);
        }
    }

    /**
     * Call object's method to get specific object's value and compare it with JSON attribute.
     * Object's value is stored in the attribute with same name as JSON attribute name.
     *
     * @param packageName
     */
    public void pushSubTypesOf(String packageName) {
        try {
            Method getter = Objects.getGetter(baseClass, getWatchedAttribute());
            pushSubTypesOf(packageName, getter);
        } catch (Exception e) {
            throw new RuntimeException("I cant find getter", e);
        }
    }

    public Class<T> getBaseClass() {
        return baseClass;
    }

    @Override
    protected Class<? extends T> searchForClass(String key) {
        // Toto synchronizovat netreba, pretoze ak by doslo k pristupu ku zdielanemu zdroju = registry
        // Tak by sa zapisala pre dany kluc ta ista hodnota co nie je chyba
        Class<? extends T> aClass = super.searchForClass(key);
        if (!mTryLongestSubsequence) {
            return aClass;
        }
        if (aClass == null) {
            LOGGER.info("Cannot find class for key '" + key + "', trying longest subsequnce.");
            //noinspection NullableProblems
            String longestString = Strings.findLongestPrefix(keySet(), key, new Function<String, String>() {
                @Nullable
                @Override
                public String apply(@Nullable String input) {
                    return input;
                }
            });
            if (longestString != null) {
                aClass = super.searchForClass(longestString);
                register(key, aClass);
                LOGGER.info("Finded longest subsequnce for '" + key + "' as '" + longestString + "'. Registering to class '" + aClass.getName());
            }
        }
        return aClass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("baseClass", baseClass).append("mTryLongestSubsequence", mTryLongestSubsequence).toString();
    }
}
