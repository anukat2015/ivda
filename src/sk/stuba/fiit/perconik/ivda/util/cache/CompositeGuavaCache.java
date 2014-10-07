package sk.stuba.fiit.perconik.ivda.util.cache;

import java.io.Serializable;

/**
 * Created by Seky on 7. 10. 2014.
 */
public class CompositeGuavaCache<Key, Value extends Serializable> extends GuavaCache<Key, Value> {
    private ICaching<Key, Value> another;

    public CompositeGuavaCache(ICaching<Key, Value> another) {
        this.another = another;
    }

    @Override
    public Value valueNotFound(Key key) {
        return another.get(key);
    }
}
