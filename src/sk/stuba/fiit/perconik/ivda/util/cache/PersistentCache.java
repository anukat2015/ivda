package sk.stuba.fiit.perconik.ivda.util.cache;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by Seky on 5. 9. 2014.
 */
@ThreadSafe
public abstract class PersistentCache<Key, Value extends Serializable> {
    private static final Logger LOGGER = Logger.getLogger(PersistentCache.class.getName());
    private final File cacheFolder;

    protected PersistentCache(File cacheDir) {
        cacheFolder = cacheDir;
    }

    public static String computeUID(String x) {
        return Hex.encodeHexString(DigestUtils.sha1(x));
    }

    public File getCacheFolder() {
        return cacheFolder;
    }

    protected boolean isCacheAllowed(Key key) {
        return true;
    }

    public final Value get(Key key) {
        // Cache nie je povolena pre specificky kluc
        if (!isCacheAllowed(key)) {
            return fileNotFound(key);
        }

        // Pokracuj dalej pokusom sa nacitat cache
        Value response = null;
        FileLock lock = null;
        File cacheFile = computeFilePath(cacheFolder, key);

        try {
            RandomAccessFile raf = new RandomAccessFile(cacheFile, "rw");
            FileChannel channel = raf.getChannel();
            lock = channel.lock();
            try {
                response = deserialize(raf.getFD());
                LOGGER.info("Deserializing from " + cacheFile);
                return response;
            } catch (Exception e) {
                // Chyba pri deserializacii, vymaz teda objekt a uloz ho znova
                cacheFile.delete();
            }
            response = fileNotFound(key);
            serialize(raf.getFD(), response);
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

    /**
     * Should be thread safe!
     *
     * @param fd
     * @return
     */
    protected Value deserialize(FileDescriptor fd) {
        return (Value) SerializationUtils.deserialize(new BufferedInputStream(new FileInputStream(fd)));
    }

    /**
     * Should be thread safe!
     *
     * @param fd
     * @param v
     */
    protected void serialize(FileDescriptor fd, Value v) {
        SerializationUtils.serialize(v, new FileOutputStream(fd));
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
        return new File(folder, computeUID(key.toString()));
    }
}
