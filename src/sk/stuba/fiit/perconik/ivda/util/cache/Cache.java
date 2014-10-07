package sk.stuba.fiit.perconik.ivda.util.cache;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.io.Serializable;

/**
 * Created by Seky on 7. 10. 2014.
 */
public abstract class Cache<K, V extends Serializable> implements ICaching<K, V> {
    private static final Logger LOGGER = Logger.getLogger(Cache.class.getName());

    public Cache() {
        if (!isCacheEnabled()) {
            LOGGER.warn("Caching is disabled!");
        }
    }

    @Override
    public boolean isCacheEnabled() {
        return Configuration.getInstance().getCacheEnabled();
    }

    @Override
    public V get(K uri) {
        if (!isCacheEnabled()) {
            return valueNotFound(uri);
        }
        return getDirect(uri);
    }

    protected abstract V getDirect(final K uri);

    @Override
    public abstract V valueNotFound(K key);

    protected String computeUniqueID(K key) {
        return key.toString();
    }

}
