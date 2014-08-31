package sk.stuba.fiit.perconik.ivda.uaca.cache;

import sk.stuba.fiit.perconik.ivda.uaca.client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.uaca.client.WebClient;

import java.net.URI;

/**
 * Created by Seky on 31. 8. 2014.
 */
private final class MyGuavaFilesCache extends GuavaFilesCache<URI, PagedResponse<T>> {
    private static final long serialVersionUID = -2771011404900728777L;
    private final WebClient client;

    protected MyGuavaFilesCache() {
        super(CACHE_FOLDER);
    }

    @Override
    protected PagedResponse<T> fileNotFound(URI uri) {
        //noinspection unchecked
        return (PagedResponse<T>) client.synchronizedRequest(uri, mClass);
    }
}
