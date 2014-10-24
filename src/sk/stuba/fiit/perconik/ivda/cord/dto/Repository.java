package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class Repository implements Serializable {
    private static final long serialVersionUID = 7987038385630834758L;
    private String name;
    private URL url;

    /**
     * time of the last repository update call
     */
    private Date lastUpdateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repository that = (Repository) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("lastUpdateTime", lastUpdateTime).append("name", name).append("url", url).toString();
    }
}
