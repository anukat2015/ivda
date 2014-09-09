package sk.stuba.fiit.perconik.ivda.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 9. 8. 2014.
 * <p/>
 * Cachuj informacie pomocou Guava kniznice. Pricom perzistentne uloz hodnoty na zaklade klucov.
 */

@ThreadSafe
@SuppressWarnings("TypeParameterNamingConvention")
public abstract class GuavaFilesCache<Key, Value extends Serializable> extends PersistentCache<Key, Value> {
    private static final Logger LOGGER = Logger.getLogger(GuavaFilesCache.class.getName());
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final LoadingCache<Key, Value> cache;

    @SuppressWarnings({"OverridableMethodCallDuringObjectConstruction", "OverriddenMethodCallDuringObjectConstruction"})
    protected GuavaFilesCache(File cacheDir) {
        super(cacheDir);
        cache = buildCache();
    }

    protected LoadingCache<Key, Value> buildCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(100L)
                .expireAfterWrite(1L, TimeUnit.HOURS)
                .build(new CacheLoader<Key, Value>() {
                    @Override
                    public Value load(@SuppressWarnings("unused") Key key) throws Exception {
                        return GuavaFilesCache.this.get(key);
                    }
                });
    }

    public final LoadingCache<Key, Value> getCache() {
        return cache;
    }
}
