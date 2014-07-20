package sk.stuba.fiit.perconik.ivda.deserializers;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Seky on 20. 7. 2014.
 */
public class PolymorhicDeserializer<T> extends CustomDeserializer<T> {
    private Class<T> baseClass;

    public PolymorhicDeserializer(Class<T> aClass, String jsonAttribute) {
        super(jsonAttribute);
        baseClass = aClass;
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
            String attribute = getWatchedAttribute();
            String name = Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
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
}
