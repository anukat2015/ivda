package sk.stuba.fiit.perconik.ivda.uaca.deserializer;

import com.google.common.base.Function;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import sk.stuba.fiit.perconik.ivda.util.Strings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Seky on 20. 7. 2014.
 * <p/>
 * Deserialize polymorfed objects by specific keys. Like a URI
 */
public class PolymorphicDeserializer<T> extends CustomDeserializer<T> {
    private static final Logger logger = Logger.getLogger(PolymorphicDeserializer.class.getName());

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
     * find longest / nearest prefix
     *
     * @param collection
     * @param search
     * @return
     */
    private static String findLongestPrefix(Collection<String> collection, String search) {
        String longestString = "";
        for (String key : collection) {
            if (search.startsWith(key)) {
                if (key.length() > longestString.length()) {
                    longestString = key;
                }
            }
        }
        return longestString;
    }

    /**
     * Call object's method to get specific object's value and compare it with JSON attribute
     *
     * @param packageName
     * @param callMethod
     */
    public void pushSubTypesOf(String packageName, Method callMethod) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(baseClass);
        if (subTypes.isEmpty()) {
            logger.info("Package '" + packageName + "' is empty.");
        }
        try {
            for (Class<? extends T> aClass : subTypes) {
                Object object = callMethod.invoke(aClass.newInstance());
                register(object.toString(), aClass);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
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
            String attributeName = getWatchedAttribute();
            String name = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
            Method getter;

            try {
                getter = baseClass.getMethod("get" + name);
            } catch (NoSuchMethodException e) {
                getter = baseClass.getMethod("is" + name);
            }
            pushSubTypesOf(packageName, getter);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("I cant find getter", e);
        }
    }

    public Class<T> getBaseClass() {
        return baseClass;
    }

    @Override
    protected Class<? extends T> searchForClass(String key) {
        Class<? extends T> aClass = super.searchForClass(key);
        if (!mTryLongestSubsequence) return aClass;
        if (aClass == null) {
            logger.info("Cannot find class for key '" + key + "', trying longest subsequnce.");
            String longestString = Strings.findLongestPrefix(registry.keySet(), key, new Function<String, String>() {
                @Override
                public String apply(String input) {
                    return input;
                }
            });
            if (longestString != null) {
                aClass = super.searchForClass(longestString);
                register(key, aClass);
                logger.info("Finded longest subsequnce for '" + key + "' as '" + longestString + "'. Registering to class '" + aClass.getName());
            }
        }
        return aClass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("baseClass", baseClass).append("mTryLongestSubsequence", mTryLongestSubsequence).toString();
    }
}
