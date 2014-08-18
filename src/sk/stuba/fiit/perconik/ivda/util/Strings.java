package sk.stuba.fiit.perconik.ivda.util;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by Seky on 16. 8. 2014.
 * <p>
 * Pomocna trieda pre retazce.
 */
public final class Strings {
    /**
     * find longest / nearest prefix
     *
     * @param collection
     * @param search
     * @param trans
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T findLongestPrefix(Collection<T> collection, String search, Function<T, String> trans) {
        T longestString = null;
        for (T object : collection) {
            String key = trans.apply(object);
            assert key != null;
            if (search.startsWith(key)) {
                int length = (longestString == null) ? 0 : trans.apply(longestString).length();
                if (key.length() > length) {
                    longestString = object;
                }
            }
        }
        return longestString;
    }
}
