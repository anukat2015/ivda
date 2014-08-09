package sk.stuba.fiit.perconik.ivda.uaca.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.GuavaFilesCache;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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
    private GuavaFilesCache<URI, PagedResponse<T>> cache;

    public DownloadAll(Class<? extends PagedResponse<T>> aClass) {
        mClass = aClass;
        client = new WebClient();
        cache = new GuavaFilesCache<URI, PagedResponse<T>>() {
            @Override
            protected File getCacheFolder() {
                return cacheFolder;
            }

            @Override
            protected File computeFilePath(File folder, URI uri) {
                try {
                    return new File(cacheFolder, "D-" + URLEncoder.encode(uri.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected PagedResponse<T> fileNotFound(URI uri) {
                return (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
            }
        };
    }

    private URI getNextURI(PagedResponse<T> response, URI uri) {
        // Stary sposob pomocou citania URL adresy v "next" policku v odpovedi nefunguje spravne
        if (!response.isHasNextPage()) {
            return null;
        }

        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        for (NameValuePair pair : pairs) {
            if (pair.getName().equals("page")) {
                Integer actual = Integer.valueOf(pair.getValue()) + 1;
                return UriBuilder.fromUri(uri).replaceQueryParam("page", actual).build();
            }
        }

        logger.error("Page key do not exist in URI.");
        return null;
    }

    protected void downloadedNonRecursive(URI uri) {
        logger.info("Starting downloading.");
        while (uri != null) {
            PagedResponse<T> response;
            response = downloadUri(uri);
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

    protected PagedResponse<T> downloadUri(URI uri) {
        return cache.getCache().getUnchecked(uri);
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

}
