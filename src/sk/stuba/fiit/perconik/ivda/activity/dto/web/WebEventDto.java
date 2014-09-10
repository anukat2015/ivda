package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import sk.stuba.fiit.perconik.ivda.activity.dto.ApplicationEventDto;

import javax.ws.rs.core.UriBuilder;

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
}