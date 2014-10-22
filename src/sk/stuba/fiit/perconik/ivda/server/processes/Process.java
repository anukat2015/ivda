package sk.stuba.fiit.perconik.ivda.server.processes;

import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;

import java.util.Date;

/**
 * Created by Seky on 21. 10. 2014.
 */
public class Process {
    private Date start;
    private Date end;
    private ProcessDto process;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinishedProcess{");
        sb.append("end=").append(end);
        sb.append(", process=").append(process);
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

    public ProcessDto getProcess() {
        return process;
    }

    public void setProcess(ProcessDto process) {
        this.process = process;
    }

    public boolean isFinished() {
        return getEnd() != null;
    }
}
