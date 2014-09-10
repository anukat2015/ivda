package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import sk.stuba.fiit.perconik.uaca.dto.web.*;

import javax.ws.rs.core.UriBuilder;

public class WebSaveDocumentEventDto extends sk.stuba.fiit.perconik.uaca.dto.web.WebEventDto {
    private String name;

    public WebSaveDocumentEventDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("savedocument");
    }
}


