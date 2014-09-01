package sk.stuba.fiit.perconik.ivda.activity.entities;

import java.io.Serializable;

/**
 * <p>
 * EventsURI bean. Attributes:
 * <p>
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
public class EventsRequestParameters implements Serializable {
    private static final long serialVersionUID = -402296428944403239L;

    protected Integer page;
    protected Integer pageSize;
    protected String timeFrom;
    protected String timeTo;
    protected String eventTypeUri;
    protected Boolean exactType;
    protected String user;
    protected String workstation;
    protected Boolean ascending;

    public EventsRequestParameters() {
        // Defaultne hodnoty ktoreme chceme
        page = 0;
        pageSize = 100;
        ascending = true;
        exactType = false;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
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
}
