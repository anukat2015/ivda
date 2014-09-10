package sk.stuba.fiit.perconik.ivda.activity.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebCopyEventDto extends WebEventDto {
    private static final long serialVersionUID = -3599193333584765740L;
    private String tabId;
    private String content;

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