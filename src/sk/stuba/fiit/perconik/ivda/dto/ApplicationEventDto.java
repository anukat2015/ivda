package sk.stuba.fiit.perconik.ivda.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ApplicationEventDto extends EventDto {
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
/*@Override
    protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("application");
	}*/

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("appName", appName).append("appVersion", appVersion).append("sessionId", sessionId).toString();
    }
}
