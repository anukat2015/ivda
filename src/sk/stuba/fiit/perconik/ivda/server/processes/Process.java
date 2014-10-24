package sk.stuba.fiit.perconik.ivda.server.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import sk.stuba.fiit.perconik.ivda.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seky on 21. 10. 2014.
 * <p/>
 * Proces spusteni v OS.
 */
public class Process implements Serializable {
    private static final long serialVersionUID = -7316143646812880569L;

    private Date start;
    private Date end;
    private String name;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Process{");
        sb.append("end=").append(end);
        sb.append(", name=").append(name);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public boolean isFinished() {
        return end != null;
    }

    @JsonIgnore
    public boolean isOverlaping(Date start2, Date end2) {
        return DateUtils.isOverlaping(start, end, start2, end2);
    }

    @JsonIgnore
    public boolean isOverlaping(Process p) {
        return isOverlaping(p.start, p.getEnd());
    }
}
