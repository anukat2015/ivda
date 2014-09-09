package sk.stuba.fiit.perconik.ivda.util.cache;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;

/**
 * Created by Seky on 9. 9. 2014.
 */
public abstract class OfyCache<K extends Key<T>, T> {
    public static String computeUID(String x) {
        return Hex.encodeHexString(DigestUtils.sha1(x));
    }

    protected boolean isCacheAllowed(K key) {
        return true;
    }

    /*
      Key.create(ChunkOfEvents.class, 10)
     */
    public final T get(final K key) {
        // Cache nie je povolena pre specificky kluc
        if (!isCacheAllowed(key)) {
            return valueNotFound(key);
        }

        final ArrayList<T> ev = new ArrayList<T>();
        OfyService.ofy().transact(new VoidWork() {
            public void vrun() {
                T item = OfyService.ofy().load().key(key).now();
                if (item == null) {
                    item = valueNotFound(key);
                    OfyService.ofy().save().entity(item).now();
                }
                ev.clear();   // tranzakcia sa moze opakovat
                ev.add(item);
            }
        });
        return ev.get(0);
    }

    protected abstract T valueNotFound(K key);
}
