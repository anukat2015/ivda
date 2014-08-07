package sk.stuba.fiit.perconik.ivda.uaca.dto;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

public class ProcessesChangedSinceCheckEventDto extends SystemEventDto {
    private List<ProcessDto> startedProcesses;
    private List<ProcessDto> killedProcesses;

    public ProcessesChangedSinceCheckEventDto() {
    }

    public List<ProcessDto> getStartedProcesses() {
        if (this.startedProcesses != null) {
            return this.startedProcesses;
        }
        return new ArrayList<ProcessDto>();
    }

    public void setStartedProcesses(List<ProcessDto> startedProcesses) {
        this.startedProcesses = startedProcesses;
    }

    public List<ProcessDto> getKilledProcesses() {
        if (this.killedProcesses != null) {
            return this.killedProcesses;
        }

        return new ArrayList<ProcessDto>();
    }

    public void setKilledProcesses(List<ProcessDto> killedProcesses) {
        this.killedProcesses = killedProcesses;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("processeschangedsincechceck");
    }
}
