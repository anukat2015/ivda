package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class Change implements Serializable {
    private URL url;
    private ChangeType ancestor1ChangeType;
    private ChangeType ancestor2ChangeType;

    public Change() {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ChangeType getAncestor1ChangeType() {
        return ancestor1ChangeType;
    }

    public void setAncestor1ChangeType(ChangeType ancestor1ChangeType) {
        this.ancestor1ChangeType = ancestor1ChangeType;
    }

    public ChangeType getAncestor2ChangeType() {
        return ancestor2ChangeType;
    }

    public void setAncestor2ChangeType(ChangeType ancestor2ChangeType) {
        this.ancestor2ChangeType = ancestor2ChangeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Change change = (Change) o;

        if (url != null ? !url.equals(change.url) : change.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
