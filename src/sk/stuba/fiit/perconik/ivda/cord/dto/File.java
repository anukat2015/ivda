package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class File implements Serializable {
    private URL url;
    private String lastCommit;
    private URI versionUri;
    private File ancestor1;

    public File() {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
    }

    public URI getVersionUri() {
        return versionUri;
    }

    public void setVersionUri(URI versionUri) {
        this.versionUri = versionUri;
    }

    public File getAncestor1() {
        return ancestor1;
    }

    public void setAncestor1(File ancestor1) {
        this.ancestor1 = ancestor1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        if (url != null ? !url.equals(file.url) : file.url != null) return false;
        if (versionUri != null ? !versionUri.equals(file.versionUri) : file.versionUri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (versionUri != null ? versionUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ancestor1", ancestor1).append("lastCommit", lastCommit).append("url", url).append("versionUri", versionUri).toString();
    }
}
