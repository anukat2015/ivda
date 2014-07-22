package sk.stuba.fiit.perconik.ivda.Client;

import org.apache.log4j.Logger;

import java.net.URI;

/**
 * Created by Seky on 22. 7. 2014.
 * Neskor trieda moze asynchronne volat ...
 */
public abstract class DownloadAll<T> {
    private static final Logger logger = Logger.getLogger(DownloadAll.class.getName());
    private WebClient client;
    private Class<?> mClass;

    public DownloadAll(URI uri, Class<?> aClass) {
        client = new WebClient();
        mClass = aClass;

        // Start downloading ...
        downloadedNonRecursive(uri);
    }

    private URI getNextURI(PagedResponse<T> response) {
        for (Link link : response.getLinks()) {
            if (link.getRel().equals("next")) {
                return link.getHref();
            }
        }
        if (response.isHasNextPage()) {
            logger.error("Link with rel=next not found.");
        }
        return null;
    }

    private void downloadedNonRecursive(URI uri) {
        while (uri != null) {
            PagedResponse<T> response;
            response = (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
            if (!downloaded(response)) {
                break;
            }
            uri = getNextURI(response);
        }
    }


    protected abstract boolean downloaded(PagedResponse<T> response);
}
