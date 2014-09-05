package sk.stuba.fiit.perconik.ivda.cord.dto;

import com.ibm.icu.util.GregorianCalendar;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class Repository implements Serializable {
    private String name;
    private URL url;
    private GregorianCalendar lastUpdateTime;

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

    public GregorianCalendar getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(GregorianCalendar lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
