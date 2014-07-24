package sk.stuba.fiit.perconik.ivda.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;

public class EventDto {
    public static final String BASE_URI = "http://perconik.gratex.com/useractivity";

    @JsonSerialize(using = ToStringSerializer.class)
    private XMLGregorianCalendar timestamp;
    private String eventId;
    private String user;
    private String workstation;
    private URI eventTypeUri = getDefaultEventTypeUri().build();
    private boolean wasCommitForcedByUser = false; //true - commit forced by 'send now' button

    public EventDto() {
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public XMLGregorianCalendar getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(XMLGregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWorkstation() {
        return this.workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    public URI getEventTypeUri() {
        return this.eventTypeUri;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventDto eventDto = (EventDto) o;

        if (eventId != null ? !eventId.equals(eventDto.eventId) : eventDto.eventId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0;
    }
}