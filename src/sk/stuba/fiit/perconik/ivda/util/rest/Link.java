package sk.stuba.fiit.perconik.ivda.util.rest;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Seky on 19. 7. 2014.
 * <p>
 * Datova entita pre Link vrateny v UACA klientovi.
 */
public final class Link implements Serializable {
    private static final long serialVersionUID = -8976006536157835573L;
    private String rel;
    private URL href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("href", href).append("rel", rel).toString();
    }

}
