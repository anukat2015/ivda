package sk.stuba.fiit.perconik.ivda.server.servlets;

import java.util.List;

/**
 * Created by Seky on 11. 9. 2014.
 */
public class TimelineResponse {
    private String status = "ok";
    private List<TimelineEvent> events;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TimelineEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TimelineEvent> events) {
        this.events = events;
    }
}