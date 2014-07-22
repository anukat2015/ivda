package sk.stuba.fiit.perconik.ivda.Client;

import com.gratex.perconik.useractivity.app.dto.EventDto;
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

    public static void main(String[] args) {

        URI link = new EventsRequest().setParameters(null).getURI();

        DownloadAll<EventDto> download = new DownloadAll<EventDto>(link, EventsResponse.class) {
            int counter = 0;

            @Override
            protected boolean downloaded(PagedResponse<EventDto> response) {
                counter++;
                if (counter == 3) return false;
                logger.info(response.getResultSet().toString());
                return true;
            }
        };
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
