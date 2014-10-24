package sk.stuba.fiit.perconik.ivda.util.cache.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import sk.stuba.fiit.perconik.ivda.util.cache.Cache;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Seky on 5. 10. 2014.
 */
@ThreadSafe
public abstract class OfyDynamicCache<K, Value extends Serializable> extends Cache<K, Value> {

    @SuppressWarnings("unchecked")
    @Override
    protected final Value getDirect(final K uri) {
        final String uid = computeUniqueID(uri);
        final Key<OfyBlob> key = Key.create(OfyBlob.class, uid);
        final ArrayList<OfyBlob> ev = new ArrayList<>();
        OfyService.ofy().transact(new VoidWork() {
            public void vrun() {
                OfyBlob item = OfyService.ofy().load().key(key).now();
                if (item == null) {
                    item = new OfyBlob(uid, valueNotFound(uri));
                    if (item == null) { // chyba pri serializacii
                        return;
                    }
                    OfyService.ofy().save().entity(item).now();
                }
                ev.clear();   // tranzakcia sa moze opakovat
                ev.add(item);
            }
        });
        if (ev.isEmpty()) {
            return null;
        }
        return (Value) ev.get(0).getData();
    }

    @Override
    public abstract Value valueNotFound(K key);
}
