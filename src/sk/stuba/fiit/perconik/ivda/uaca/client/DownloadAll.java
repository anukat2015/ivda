package sk.stuba.fiit.perconik.ivda.uaca.client;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Seky on 22. 7. 2014.
 * Neskor trieda moze asynchronne volat ...
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

    private URI getNextURI(PagedResponse<T> response) {
        for (Link link : response.getLinks()) {
            if (link.getRel().equals("next")) {
                URI uri = link.getHref();
                try {
                    uri = new URI( uri.toString().replace("steltecia/", "steltecia%5") );
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return uri;
            }
        }
        if (response.isHasNextPage()) {
            logger.error("Link with rel=next not found.");
        }
        return null;
    }

    protected void downloadedNonRecursive(URI uri) {
        logger.info("Starting downloading.");
        while (uri != null) {
            PagedResponse<T> response;
            response = downloadUrl(uri);
            if(response.getResultSet().isEmpty()) {
                logger.warn("Resultset is empty! Bad request?");
            }
            if (!downloaded(response)) {
                logger.info("Downloading canceled.");
                canceled();
                break;
            }
            uri = getNextURI(response);
        }
        logger.info("Downloading finished.");
        finished();
    }

    private PagedResponse<T> downloadUrl(URI uri) {
        PagedResponse<T> response = null;
        String cacheName = Integer.toString(uri.hashCode());
        File cacheFile = new File(cacheFolder, cacheName);

        FileInputStream file = null;
        try {
            // Deserialize
            file = new FileInputStream(cacheFile);
            try {
                logger.info("Deserializing from " + cacheFile);
                response = (PagedResponse<T>) SerializationUtils.deserialize(file);
                logger.info("Deserializing finished.");
                file.close();
            } catch (Exception e) {
                file.close();
                logger.info("Deleting cache file.");
                cacheFile.delete();
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            response = (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
            serialize(response, cacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    protected abstract boolean downloaded(PagedResponse<T> response);

    protected void canceled() {

    }

    protected void finished() {
    }

    protected void serialize(PagedResponse<T> response, File cacheFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(cacheFile);
            logger.info("Serializing to " + cacheFile);
            SerializationUtils.serialize(response, fos);
            logger.info("Serializing finished.");
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
