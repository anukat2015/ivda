package sk.stuba.fiit.perconik.ivda.activity.dto;

import javax.ws.rs.core.UriBuilder;
import java.util.Collections;
import java.util.List;

public class ProcessesChangedSinceCheckEventDto extends SystemEventDto {
    private static final long serialVersionUID = -7574345404251439398L;
    private List<ProcessDto> startedProcesses;
    private List<ProcessDto> killedProcesses;

    public List<ProcessDto> getStartedProcesses() {
        if (startedProcesses != null) {
            return startedProcesses;
        }
        return Collections.emptyList();
    }

    public void setStartedProcesses(List<ProcessDto> startedProcesses) {
        this.startedProcesses = startedProcesses;
    }

    public List<ProcessDto> getKilledProcesses() {
        if (killedProcesses != null) {
            return killedProcesses;
        }

        return Collections.emptyList();
    }

    public void setKilledProcesses(List<ProcessDto> killedProcesses) {
        this.killedProcesses = killedProcesses;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("processeschangedsincechceck");
    }
}
