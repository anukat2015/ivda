package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebSaveDocumentEventDto extends WebEventDto {
    private static final long serialVersionUID = 6739705374264580583L;
    private String name;

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


