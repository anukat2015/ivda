package sk.stuba.fiit.perconik.ivda.cord.dto;

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
}
