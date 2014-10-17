package sk.stuba.fiit.perconik.ivda.activity.client;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;

/**
 * <p/>
 * EventsURI bean. Attributes:
 * <p/>
 * Page [int]
 * Zero based index of the results page
 * PageSize [int]
 * Demanded size of the results page. Default page size is 20 items. Limited by server configuration to 100 items
 * TimeFrom [nullable DateTime]
 * Search for events with Timestamp later than TimeFrom
 * TimeTo [nullable DateTime]
 * Search for events with Timestamp earlier than TimeTo
 * EventTypeUri [string]
 * Search for events of types defined by URIs, which start with EventTypeUri - allows hierarchical type filtering
 * ExactType [bool]
 * Search for events of type defined by exact URI. Default is false
 * User [string]
 * Search for events from users with names starting with given string
 * Workstation [string]
 * Search for events from workstations with names starting with given string
 * Ascending [bool]
 * Defines results order. Results are ordered by Event Timestamp.
 * Default is false, which means results are ordered descending or from the newest to the oldest events
 * If true, results are ordered ascending or from the oldest to the newest events
 */
public class EventsRequest implements Serializable {
    private static final long serialVersionUID = -402296428944403239L;

    protected Integer pageIndex;
    protected Integer pageSize;
    protected String timeFrom;
    protected String timeTo;
    protected String eventTypeUri;
    protected Boolean exactType;
    protected String user;
    protected String workstation;
    protected Boolean ascending;

    public EventsRequest() {
        // Defaultne hodnoty ktoreme chceme
        pageIndex = 0;
        pageSize = 100;
        ascending = true;
        exactType = false;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public String getEventTypeUri() {
        return eventTypeUri;
    }

    public void setEventTypeUri(URI type) {
        eventTypeUri = type.toString();
    }

    public void setEventTypeUri(EventDto event) {
        setEventTypeUri(event.getEventTypeUri());
    }

    public Boolean getExactType() {
        return exactType;
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

    public Boolean getAscending() {
        return ascending;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }

    public void setTime(Date from, Date to) {
        timeFrom = DateUtils.toString(from);
        timeTo = DateUtils.toString(to);
    }

    public void setEventTypeUri(EventDto event, String subtype) {
        setEventTypeUri(UriBuilder.fromUri(event.getEventTypeUri()).path(subtype).build());
    }
}
