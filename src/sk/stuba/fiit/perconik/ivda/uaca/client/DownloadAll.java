package sk.stuba.fiit.perconik.ivda.uaca.client;

import org.apache.commons.lang.SerializationUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * HTTP client pre sluzbu UACA. Ktory stahnie prvu stranku, poskytne udaje, stiahne druhu stranu atd.
 * Client cachuje odpovede do cacheFolder zlozky.
 * <p/>
 * TODO: Neskor trieda moze asynchronne volat ...
 */
public abstract class DownloadAll<T extends Serializable> implements Serializable {
    private static final Logger logger = Logger.getLogger(DownloadAll.class.getName());
    private final static File cacheFolder = new File("C:/cache/");

    private WebClient client;
    private Class<? extends PagedResponse<T>> mClass;

    public DownloadAll(Class<? extends PagedResponse<T>> aClass) {
        client = new WebClient();
        mClass = aClass;
    }

    private URI getNextURI(PagedResponse<T> response, URI uri) {
        // Stary sposob pomocou citania URL adresy v "next" policku v odpovedi nefunguje spravne
        if (!response.isHasNextPage()) {
            return null;
        }

        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        Integer actual;
        for (NameValuePair pair : pairs) {
            if (pair.getName().equals("page")) {
                actual = Integer.valueOf(pair.getValue());
                String txtURI = uri.toString();
                Integer next = actual + 1;
                txtURI = txtURI.replace("page=" + actual, "page=" + next);
                return URI.create(txtURI);
            }
        }

        logger.error("Page key do not exist in URI.");
        return null;
    }

    protected void downloadedNonRecursive(URI uri) {
        logger.info("Starting downloading.");
        while (uri != null) {
            PagedResponse<T> response;
            response = downloadUrl(uri);
            if (response.getResultSet().isEmpty()) {
                logger.warn("Resultset is empty! Bad request?");
            }
            if (!downloaded(response)) {
                logger.info("Downloading canceled.");
                canceled();
                break;
            }
            uri = getNextURI(response, uri);
        }
        logger.info("Downloading finished.");
        finished();
    }

    private PagedResponse<T> downloadUrl(URI uri) {
        String cacheName = Integer.toString(uri.hashCode());
        File cacheFile = new File(cacheFolder, cacheName);
        PagedResponse<T> response = null;

        try {
            response = deserialize(cacheFile, uri);
        } catch (FileNotFoundException f) {
            response = (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
            serialize(cacheFile, response);
        }
        return response;
    }

    private PagedResponse<T> deserialize(File cacheFile, URI uri) throws FileNotFoundException {
        try {
            // Deserialize
            FileInputStream file = new FileInputStream(cacheFile);
            try {
                PagedResponse<T> response;
                logger.info("Deserializing from " + cacheFile);
                response = (PagedResponse<T>) SerializationUtils.deserialize(file);
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

    /**
     * Spracuj odpoved vo vlastnej metode.
     *
     * @param response
     * @return true - pokracuj v stahovani dalej
     */
    protected abstract boolean downloaded(PagedResponse<T> response);

    protected void canceled() {

    }

    protected void finished() {
    }

    private void serialize(File cacheFile, PagedResponse<T> response) {
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
