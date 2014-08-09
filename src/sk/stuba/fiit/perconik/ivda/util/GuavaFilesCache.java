package sk.stuba.fiit.perconik.ivda.util;

import com.google.common.cache.*;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 9. 8. 2014.
 */
public abstract class GuavaFilesCache<Key, Value extends Serializable> {
    protected static final Logger logger = Logger.getLogger(GuavaFilesCache.class.getName());
    private final File cacheFolder;
    private final LoadingCache<Key, Value> cache;

    public GuavaFilesCache() {
        cacheFolder = getCacheFolder();
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(200)
                .expireAfterWrite(3, TimeUnit.HOURS)
                .removalListener(new RemovalListener<Key, Value>() {

                    @Override
                    public void onRemoval(RemovalNotification<Key, Value> notification) {
                        File cacheFile = computeFilePath(cacheFolder, notification.getKey());
                        cacheFile.delete();
                    }
                })
                .build(new CacheLoader<Key, Value>() {
                    @Override
                    public Value load(@SuppressWarnings("unused") final Key key) throws Exception {
                        return loadFromFile(key);
                    }
                });
    }

    public LoadingCache<Key, Value> getCache() {
        return cache;
    }

    protected abstract File getCacheFolder();

    protected Value loadFromFile(final Key key) throws Exception {
        Value response = null;
        File cacheFile = computeFilePath(cacheFolder, key);

        try {
            response = deserialize(cacheFile);
        } catch (FileNotFoundException f) {
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
    protected abstract File computeFilePath(final File folder, Key key);

    protected Value deserialize(File cacheFile) throws Exception {
        try {
            // Deserialize
            FileInputStream file = new FileInputStream(cacheFile);
            try {
                Value response;
                logger.info("Deserializing from " + cacheFile);
                response = (Value) SerializationUtils.deserialize(file);
                file.close();
                return response;
            } catch (Exception e) {
                // Chyba pri deserializaciii
                file.close();
                logger.info("Deleting cache file.");
                cacheFile.delete();
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void serialize(File cacheFile, Value response) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(cacheFile);
            logger.info("Serializing to " + cacheFile);
            SerializationUtils.serialize(response, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
