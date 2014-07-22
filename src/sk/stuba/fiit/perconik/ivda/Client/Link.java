package sk.stuba.fiit.perconik.ivda.Client;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by Seky on 19. 7. 2014.
 */
public class Link implements Serializable {
    private String rel;
    private URI href;

    public Link() {
    }

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
