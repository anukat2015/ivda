package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebNavigateEventDto extends WebEventDto {
    private static final long serialVersionUID = -9152982068522928478L;
    private String transitionTypeUri;
    private String tabId;

    public String getTransitionTypeUri() {
        return transitionTypeUri;
    }

    public void setTransitionTypeUri(String type) {
        transitionTypeUri = type;
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