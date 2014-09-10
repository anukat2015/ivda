package sk.stuba.fiit.perconik.ivda.activity.dto;

public class ApplicationEventDto extends EventDto {
    private static final long serialVersionUID = 984795989632643746L;
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

/* Nepouziva sa ...
@Override
protected UriBuilder getDefaultEventTypeUri() {
return super.getDefaultEventTypeUri().path("application");
}*/
}
