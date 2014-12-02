package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import com.google.appengine.labs.repackaged.com.google.common.base.Strings;
import sk.stuba.fiit.perconik.ivda.activity.dto.ApplicationEventDto;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URL;

public class WebEventDto extends ApplicationEventDto {
    private static final long serialVersionUID = -871209013208536985L;
    private String url;   // do prehliadaca nemusi zadat len URL!

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("web");
    }

    public String getDomain() {
        if (Strings.isNullOrEmpty(url)) {
            return url;
        }
        try {
            String host = new URL(url).getHost();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (MalformedURLException e) {
            return url;
        }
    }
}
