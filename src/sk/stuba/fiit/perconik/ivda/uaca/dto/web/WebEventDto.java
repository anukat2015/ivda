package sk.stuba.fiit.perconik.ivda.uaca.dto.web;

import sk.stuba.fiit.perconik.ivda.uaca.dto.ApplicationEventDto;

import javax.ws.rs.core.UriBuilder;

public class WebEventDto extends ApplicationEventDto {
    private String url;   // do prehliadaca nemusi zadat len URL!

    public WebEventDto() {
    }

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