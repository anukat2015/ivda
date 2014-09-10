package sk.stuba.fiit.perconik.ivda.activity.dto;

import sk.stuba.fiit.perconik.uaca.dto.*;

public class ApplicationEventDto extends sk.stuba.fiit.perconik.uaca.dto.EventDto {
    private String appName;
    private String appVersion;
    private String sessionId;

    public ApplicationEventDto() {
    }

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
