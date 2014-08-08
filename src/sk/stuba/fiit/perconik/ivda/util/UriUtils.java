package sk.stuba.fiit.perconik.ivda.util;

import javax.ws.rs.core.UriBuilder;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Seky on 8. 8. 2014.
 */
public final class UriUtils {
    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static UriBuilder addBeanProperties(UriBuilder builder, Class<?> beanClass, Object object) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                Object value = pd.getReadMethod().invoke(object);
                if (value != null) {
                    builder.queryParam(pd.getName(), URLEncoder.encode(value.toString(), "UTF-8"));
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder;
    }


}
