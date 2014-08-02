package sk.stuba.fiit.perconik.ivda.dto.ide;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;

public class RcsServerDto implements Serializable {
    /**
     * Server url
     */
    private URI url;

    /**
     * Type of the server.
     * Format should follow : http://perconik.gratex.com/useractivity/enum/rcsserver/type#[value] where value is "git", "tfs", ...
     */
    private URI typeUri;


    public RcsServerDto() {
    }

    public RcsServerDto(URI url, URI typeUri) {
        super();
        this.url = url;
        this.typeUri = typeUri;
    }

    /**
     * @return the {@link #url}
     */
    public URI getUrl() {
        return url;
    }

    /**
     * @param {@link #url}
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * @return the {@link #typeUri}
     */
    public URI getTypeUri() {
        return typeUri;
    }

    /**
     * @param {@link #typeUri}
     */
    public void setTypeUri(URI typeUri) {
        this.typeUri = typeUri;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
