package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import sk.stuba.fiit.perconik.ivda.activity.dto.web.*;

import javax.ws.rs.core.UriBuilder;

public class WebNavigateEventDto extends sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto {
    private String transitionTypeUri;
    private String tabId;

    public WebNavigateEventDto() {
    }

    public String getTransitionTypeUri() {
        return transitionTypeUri;
    }

    public void setTransitionTypeUri(String type) {
        this.transitionTypeUri = type;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("navigate");
    }
}