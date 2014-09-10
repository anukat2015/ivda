package sk.stuba.fiit.perconik.ivda.activity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.activity.deserializer.DateDeserializer;

import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;

public class EventDto implements Serializable {
    private static final String BASE_URI = "http://perconik.gratex.com/useractivity";
    private static final long serialVersionUID = 6501705290672225644L;

    @JsonDeserialize(using = DateDeserializer.class)
    private Date timestamp;
    private String eventId;
    private String user;
    private String workstation;
    private URI eventTypeUri = getDefaultEventTypeUri().build();
    private boolean wasCommitForcedByUser; //true - commit forced by 'send now' button

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWorkstation() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    public URI getEventTypeUri() {
        return eventTypeUri;
    }

    public void setEventTypeUri(URI eventTypeUri) {
        this.eventTypeUri = eventTypeUri;
    }

    public boolean getWasCommitForcedByUser() {
        return wasCommitForcedByUser;
    }

    public String getActionName() {
        String path = eventTypeUri.getPath();
        return path.substring(path.lastIndexOf('/') + 1, path.length());
    }

    protected UriBuilder getDefaultEventTypeUri() {
        return UriBuilder.fromPath(BASE_URI).path("event");
    }

    public boolean isWasCommitForcedByUser() {
        return wasCommitForcedByUser;
    }

    public void setWasCommitForcedByUser(boolean wasCommitForcedByUser) {
        this.wasCommitForcedByUser = wasCommitForcedByUser;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDto eventDto = (EventDto) o;

        if (eventId != null ? !eventId.equals(eventDto.eventId) : eventDto.eventId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0;
    }
}