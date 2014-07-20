package sk.stuba.fiit.perconik.ivda.dto;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by Seky on 19. 7. 2014.
 */
public class Link implements Serializable{
    private String Rel;
    private URI Href;

    public String getRel() {
        return Rel;
    }

    public void setRel(String rel) {
        Rel = rel;
    }

    public URI getHref() {
        return Href;
    }

    public void setHref(URI href) {
        Href = href;
    }

    @Override
    public String toString() {
        return "Link{" +
                "Rel='" + Rel + '\'' +
                ", Href=" + Href +
                '}';
    }
}
