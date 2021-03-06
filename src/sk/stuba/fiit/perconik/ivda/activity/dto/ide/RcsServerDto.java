package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;

public class RcsServerDto implements Serializable {
    private static final long serialVersionUID = -6135389534993466305L;
    /**
     * Server url
     */
    private String url;

    /**
     * Type of the server.
     * Format should follow : http://perconik.gratex.com/useractivity/enum/rcsserver/type#[value] where value is "git", "tfs", ...
     */
    private URI typeUri;


    public RcsServerDto() {
    }

    public RcsServerDto(String url, URI typeUri) {
        super();
        this.url = url;
        this.typeUri = typeUri;
    }

    /**
     * @return the {@link #url}
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param {@link #url}
     */
    public void setUrl(String url) {
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
