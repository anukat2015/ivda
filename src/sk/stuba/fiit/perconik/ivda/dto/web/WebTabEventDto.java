package sk.stuba.fiit.perconik.ivda.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebTabEventDto extends WebEventDto {
    private String tabId;

    public WebTabEventDto() {
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setEventType(String eventType) {
        setEventTypeUri(UriBuilder.fromUri(getEventTypeUri()).path(eventType).build());
    }

    @Override

    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("tab");
    }
}