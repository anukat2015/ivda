package sk.stuba.fiit.perconik.ivda.util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Seky on 8. 8. 2014.
 * <p/>
 * Pomocna trieda pre rpacovanie s URI.
 */
public final class UriUtils {
    public static String decode(HttpServletRequest req, String key) throws Exception {
        String param = req.getParameter(key);
        if (param == null) {
            return null;
        }
        return URLDecoder.decode(param, "UTF-8");
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        return splitQuery(url.getQuery());
    }

    public static Map<String, String> splitQuery(URI url) throws UnsupportedEncodingException {
        return splitQuery(url.getQuery());
    }

    protected static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> qpairs = new LinkedHashMap<>(16);
        @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int part = pair.indexOf('=');
            qpairs.put(URLDecoder.decode(pair.substring(0, part), "UTF-8"), URLDecoder.decode(pair.substring(part + 1), "UTF-8"));
        }
        return qpairs;
    }

    public static UriBuilder addBeanProperties(UriBuilder builder, Object object) throws IntrospectionException, InvocationTargetException, UnsupportedEncodingException, IllegalAccessException {
        return addBeanProperties(builder, object, object.getClass());
    }

    /**
     * You can specifi bean.
     *
     * @param builder
     * @param object
     * @param beanClass
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws UnsupportedEncodingException
     * @throws IllegalAccessException
     */
    public static UriBuilder addBeanProperties(UriBuilder builder, Object object, Class<?> beanClass) throws IntrospectionException, InvocationTargetException, UnsupportedEncodingException, IllegalAccessException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if ("class".equals(pd.getName())) {
                continue;
            }
            Object value = pd.getReadMethod().invoke(object);
            if (value != null) {
                builder.queryParam(pd.getName(), URLEncoder.encode(value.toString(), "UTF-8"));
            }
        }
        return builder;
    }
}
