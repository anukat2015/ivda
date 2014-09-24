package sk.stuba.fiit.perconik.ivda.server.servlets;

import java.util.List;
import java.util.Map;

/**
 * Created by Seky on 11. 9. 2014.
 */
public class TimelineResponse {
    private String status = "ok";
    private Map<String, List<TimelineEvent>> groups; // mapa zoznamu eventov pre developera

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, List<TimelineEvent>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<TimelineEvent>> groups) {
        this.groups = groups;
    }
}