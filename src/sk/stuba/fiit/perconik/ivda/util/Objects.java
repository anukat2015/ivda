package sk.stuba.fiit.perconik.ivda.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Seky on 16. 8. 2014.
 *
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

    public final void processAllGetters(Class<?> beanClass, Object object, Iterator<PropertyDescriptor> iterator) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if ("class".equals(pd.getName())) {
                continue;
            }
            iterator.
            Object value = pd.getReadMethod().invoke(object);
            if (value != null) {
                builder.queryParam(pd.getName(), URLEncoder.encode(value.toString(), "UTF-8"));
            }
        }
        return builder;
    }
}
