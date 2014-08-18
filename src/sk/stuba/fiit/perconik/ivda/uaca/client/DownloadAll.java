package sk.stuba.fiit.perconik.ivda.uaca.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * HTTP client pre sluzbu UACA. Ktory stahnie prvu stranku, poskytne udaje, stiahne druhu stranu atd.
 * Client cachuje odpovede do CACHE_FOLDER zlozky.
 * <p>
 */
public abstract class DownloadAll<T extends Serializable> implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(DownloadAll.class.getName());
    private static final File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();
    private static final long serialVersionUID = -8441631869020848898L;

    private final WebClient client;
    private final Class<? extends PagedResponse<T>> mClass;
    private final MyGuavaFilesCache cache;

    protected DownloadAll(Class<? extends PagedResponse<T>> aClass) {
        mClass = aClass;
        client = new WebClient();
        cache = new MyGuavaFilesCache();
    }

    @Nullable
    private URI getNextURI(PagedResponse<T> response, URI uri) {
        // Stary sposob pomocou citania URL adresy v "next" policku v odpovedi nefunguje spravne
        if (!response.isHasNextPage()) {
            return null;
        }

        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        for (NameValuePair pair : pairs) {
            if ("page".equals(pair.getName())) {
                Integer actual = Integer.valueOf(pair.getValue()) + 1;
                return UriBuilder.fromUri(uri).replaceQueryParam("page", actual).build();
            }
        }

        LOGGER.error("Page key do not exist in URI.");
        return null;
    }

    /**
     * TODO: Neskor trieda moze asynchronne volat ...
     *
     * @param uri adresa ktora sa ma stiahnut
     */
    public void downloadedNonRecursive(URI uri) {
        LOGGER.info("Starting downloading.");
        while (uri != null) {
            PagedResponse<T> response = downloadUri(uri);
            if (response.getResultSet().isEmpty()) {
                LOGGER.warn("Resultset is empty! Bad request?");
            }
            if (!isDownloaded(response)) {
                LOGGER.info("Downloading canceled.");
                canceled();
                break;
            }
            //noinspection ConstantConditions
            uri = getNextURI(response, uri);
        }
        LOGGER.info("Downloading finished.");
        finished();
    }

    private PagedResponse<T> downloadUri(URI uri) {
        return cache.getCache().getUnchecked(uri);
    }

    /**
     * Spracuj odpoved vo vlastnej metode.
     *
     * @param response
     * @return true - pokracuj v stahovani dalej
     */
    protected abstract boolean isDownloaded(PagedResponse<T> response);

    protected void canceled() {
    }

    protected void finished() {
    }

    private final class MyGuavaFilesCache extends GuavaFilesCache<URI, PagedResponse<T>> {
        private static final long serialVersionUID = -2771011404900728777L;

        @Override
        protected File getCacheFolder() {
            return CACHE_FOLDER;
        }

        @Override
        protected File computeFilePath(File folder, URI uri) {
            try {
                return new File(CACHE_FOLDER, URLEncoder.encode(uri.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding not supported.");
            }
        }

        @Override
        protected PagedResponse<T> fileNotFound(URI uri) {
            //noinspection unchecked
            return (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
        }
    }

}
