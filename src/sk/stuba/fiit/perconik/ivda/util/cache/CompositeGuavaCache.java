package sk.stuba.fiit.perconik.ivda.util.cache;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;

/**
 * Created by Seky on 7. 10. 2014.
 */
@ThreadSafe
public class CompositeGuavaCache<Key, Value extends Serializable> extends GuavaCache<Key, Value> {
    private ICaching<Key, Value> another;

    public CompositeGuavaCache(ICaching<Key, Value> another) {
        this.another = another;
    }

    @Override
    public final Value valueNotFound(Key key) {
        return another.get(key);
    }
}
