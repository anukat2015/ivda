package sk.stuba.fiit.perconik.ivda.uaca.client;

import com.google.common.cache.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 9. 8. 2014.
 * <p>
 * Cachuj informacie pomocou Guava kniznice. Pricom perzistentne uloz hodnoty na zaklade klucov.
 */
@SuppressWarnings("TypeParameterNamingConvention")
public abstract class GuavaFilesCache<Key, Value extends Serializable> implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(GuavaFilesCache.class.getName());
    private static final long serialVersionUID = 3013869214720455765L;
    private final File cacheFolder;
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final LoadingCache<Key, Value> cache;

    @SuppressWarnings({"AbstractMethodCallInConstructor", "OverridableMethodCallDuringObjectConstruction", "OverriddenMethodCallDuringObjectConstruction"})
    protected GuavaFilesCache(File cacheDir) {
        cacheFolder = cacheDir;
        cache = buildCache();
    }

    protected LoadingCache<Key, Value> buildCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(200L)
                .expireAfterWrite(3L, TimeUnit.HOURS)
                .removalListener(new RemovalListener<Key, Value>() {

                    @Override
                    public void onRemoval(RemovalNotification<Key, Value> notification) {
                        @SuppressWarnings("ConstantConditions") File cacheFile = computeFilePath(cacheFolder, notification.getKey());
                        //noinspection ResultOfMethodCallIgnored
                        cacheFile.delete();
                    }
                })
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
        Value response;
        File cacheFile = computeFilePath(cacheFolder, key);

        try {
            response = deserialize(cacheFile);
        } catch (FileNotFoundException e) {
            response = fileNotFound(key);
            serialize(cacheFile, response);
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
        return new File(folder, new String(DigestUtils.sha(key.toString())) );
    }

    @SuppressWarnings("unchecked")
    protected Value deserialize(File cacheFile) throws FileNotFoundException {
        // Deserialize
        FileInputStream file = new FileInputStream(cacheFile);
        try {
            LOGGER.info("Deserializing from " + cacheFile);
            Value response = (Value) SerializationUtils.deserialize(file);
            file.close();
            return response;
        } catch (Exception e) {
            // Chyba pri deserializacii, vymaz teda objekt a uloz ho znova
            try {
                file.close();
            } catch (IOException e1) {
                LOGGER.info("Cannot close file:" + cacheFile);
            }
            LOGGER.info("Deleting cache file.");
            //noinspection ResultOfMethodCallIgnored
            cacheFile.delete();
            throw new FileNotFoundException(cacheFile.toString());
        }
    }

    protected void serialize(File cacheFile, Value response) {
        try {
            FileOutputStream fos = new FileOutputStream(cacheFile);
            LOGGER.info("Serializing to " + cacheFile);
            SerializationUtils.serialize(response, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Nemozem vytvorit subor s nazvom: " + cacheFile);
        } catch (IOException e) {
            LOGGER.error("Nemozem zapisat do suboru: " + cacheFile);
        }
    }
}
