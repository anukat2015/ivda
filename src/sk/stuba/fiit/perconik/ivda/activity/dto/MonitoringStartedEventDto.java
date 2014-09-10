package sk.stuba.fiit.perconik.ivda.activity.dto;

import javax.ws.rs.core.UriBuilder;

public class MonitoringStartedEventDto extends SystemEventDto {
    private static final long serialVersionUID = 96979462078488726L;
    private String appName;
    private String appVersion;
    private String sessionId;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("monitoringstarted");
    }
}
