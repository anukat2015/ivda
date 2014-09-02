package sk.stuba.fiit.perconik.ivda.activity.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 9. 8. 2014.
 * <p>
 * Cachuj informacie pomocou Guava kniznice. Pricom perzistentne uloz hodnoty na zaklade klucov.
 */

@ThreadSafe
@SuppressWarnings("TypeParameterNamingConvention")
public abstract class GuavaFilesCache<Key, Value extends Serializable> implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(GuavaFilesCache.class.getName());
    private static final long serialVersionUID = 3013869214720455765L;
    private final File cacheFolder;
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final LoadingCache<Key, Value> cache;

    @SuppressWarnings({"OverridableMethodCallDuringObjectConstruction", "OverriddenMethodCallDuringObjectConstruction"})
    protected GuavaFilesCache(File cacheDir) {
        cacheFolder = cacheDir;
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
                        return loadFromFile(key);
                    }
                });
    }

    public final LoadingCache<Key, Value> getCache() {
        return cache;
    }

    public File getCacheFolder() {
        return cacheFolder;
    }

    protected final Value loadFromFile(Key key) {
        Value response = null;
        FileLock lock = null;
        File cacheFile = computeFilePath(cacheFolder, key);

        try {
            RandomAccessFile raf = new RandomAccessFile(cacheFile, "rw");
            FileChannel channel = raf.getChannel();
            lock = channel.lock();
            try {
                response = (Value) SerializationUtils.deserialize(new BufferedInputStream(new FileInputStream(raf.getFD())));
                LOGGER.info("Deserializing from " + cacheFile);
                return response;
            } catch (Exception e) {
                // Chyba pri deserializacii, vymaz teda objekt a uloz ho znova
                cacheFile.delete();
            }
            response = fileNotFound(key);
            SerializationUtils.serialize(response, new FileOutputStream(raf.getFD()));
        } catch (FileNotFoundException e) {
            LOGGER.error("Nemozem vytvorit subor s nazvom: " + cacheFile);
        } catch (IOException e1) {
            LOGGER.error("Nemozem zapisat do suboru: " + cacheFile);
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                }
            }
        }
        return response;
    }

    protected abstract Value fileNotFound(Key key);

    /**
     * Ked proces prevodu kluca na cestu k suboru je dlhy, moze tu byt pouzity cache.
     *
     * @param folder
     * @param key
     * @return
     */
    protected File computeFilePath(File folder, Key key) {
        return new File(folder, Hex.encodeHexString(DigestUtils.sha1(key.toString())));
    }
}
