package sk.stuba.fiit.perconik.ivda.util;

import java.lang.reflect.Method;

/**
 * Created by Seky on 16. 8. 2014.
 * <p>
 * Pomocna trieda pre pracu s reflection API.
 */
public final class Objects {
    public final static Method getGetter(Class<?> beanClass, String attributeName) throws NoSuchMethodException {
        String name = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
        Method getter;

        try {
            getter = beanClass.getMethod("get" + name);
        } catch (NoSuchMethodException e) {
            getter = beanClass.getMethod("is" + name);
        }
        return getter;
    }
}
