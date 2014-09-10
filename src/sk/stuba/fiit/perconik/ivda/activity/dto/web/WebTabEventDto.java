package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebTabEventDto extends WebEventDto {
    private static final long serialVersionUID = -4492079323415022629L;
    private String tabId;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("tab");
    }
}