package sk.stuba.fiit.perconik.ivda.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebCopyEventDto extends WebEventDto {
    private String tabId;
    private String content;

    public WebCopyEventDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("copy");
    }
}