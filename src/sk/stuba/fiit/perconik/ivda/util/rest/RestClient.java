package sk.stuba.fiit.perconik.ivda.util.rest;

import com.google.common.collect.ImmutableList;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 5. 9. 2014.
 */
public abstract class RestClient {
    private static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());

    protected final WebClient client;

    protected RestClient() {
        client = new WebClient();
    }

    protected abstract UriBuilder apiLink();

    @Nullable
    protected URI getNextURI(URI uri, String nameOfPageParamter) {
        // Stary sposob pomocou citania URL adresy v "next" policku v odpovedi nefunguje spravne
        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        for (NameValuePair pair : pairs) {
            if (nameOfPageParamter.equals(pair.getName())) {
                Integer actual = Integer.valueOf(pair.getValue()) + 1;
                return UriBuilder.fromUri(uri).replaceQueryParam(nameOfPageParamter, actual).build();
            }
        }

        LOGGER.error("Page key do not exist in URI.");
        return null;
    }

    protected void getAllPages(URI uri, Class<?> resulType, String nameOfPageParamter, IProcessPage process) {
        try {
            LOGGER.info("Starting downloading.");
            while (uri != null) {
                Paged response = (Paged) downloadUri(uri, resulType);
                if (response.isEmpty()) {
                    LOGGER.warn("Resultset is empty! Bad request?");
                    break;
                }
                process.downloaded(response);
                if (!response.isHasNextPage()) {
                    break;
                }
                //noinspection ConstantConditions
                uri = getNextURI(uri, nameOfPageParamter);
            }
            LOGGER.info("Downloading finished.");
            process.finished();
        } catch (Exception e) {
            LOGGER.error("Nemozem vygenerovat adresu alebo doslo k chybe pri stahovani.", e);
        }
    }

    protected ImmutableList downloadAll(URI uri, Class resulType, String nameOfPageParamter) {
        final List result = new ArrayList<>(150);
        getAllPages(uri, resulType, nameOfPageParamter, new IProcessPage() {
            @Override
            public void downloaded(Paged response) {
                result.addAll(response.getItems());
            }
        });
        return ImmutableList.copyOf(result);
    }

    protected Object downloadUri(URI uri, Class<?> type) {
        return client.synchronizedRequest(uri, type);
    }

    public static interface IProcessPage<T extends Paged> {
        /**
         * Spracuj odpoved vo vlastnej metode.
         *
         * @param response
         * @return true - pokracuj v stahovani dalej
         */
        public void downloaded(T response);

        public default void finished() {
        }
    }
}
