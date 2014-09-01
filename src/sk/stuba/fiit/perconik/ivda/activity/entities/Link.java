package sk.stuba.fiit.perconik.ivda.activity.entities;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by Seky on 19. 7. 2014.
 * <p>
 * Datova entita pre Link vrateny v UACA klientovi.
 */
public final class Link implements Serializable {
    private static final long serialVersionUID = -8976006536157835573L;
    private String rel;
    private URI href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("href", href).append("rel", rel).toString();
    }

}
