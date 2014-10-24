package sk.stuba.fiit.perconik.ivda.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 9. 8. 2014.
 * <p/>
 * Cachuj informacie pomocou Guava kniznice. Pricom perzistentne uloz hodnoty na zaklade klucov.
 */

@ThreadSafe
@SuppressWarnings("TypeParameterNamingConvention")
public abstract class GuavaCache<Key, Value extends Serializable> extends Cache<Key, Value> {
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final LoadingCache<Key, Value> cache;

    @SuppressWarnings({"OverridableMethodCallDuringObjectConstruction", "OverriddenMethodCallDuringObjectConstruction"})
    protected GuavaCache() {
        cache = buildCache();
    }

    protected LoadingCache<Key, Value> buildCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(100L)
                .expireAfterWrite(1L, TimeUnit.HOURS)
                .build(new CacheLoader<Key, Value>() {
                    @Override
                    public Value load(@SuppressWarnings("unused") Key key) {
                        return valueNotFound(key);
                    }
                });
    }

    @Override
    public boolean isCacheEnabled() {
        return true;
    }


    @Override
    protected Value getDirect(final Key key) {
        return cache.getUnchecked(key);
    }

}
