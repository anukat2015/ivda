package sk.stuba.fiit.perconik.ivda.activity.dto;

import sk.stuba.fiit.perconik.ivda.activity.dto.*;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

public class ProcessesChangedSinceCheckEventDto extends sk.stuba.fiit.perconik.ivda.activity.dto.SystemEventDto {
    private List<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto> startedProcesses;
    private List<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto> killedProcesses;

    public ProcessesChangedSinceCheckEventDto() {
    }

    public List<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto> getStartedProcesses() {
        if (this.startedProcesses != null) {
            return this.startedProcesses;
        }
        return new ArrayList<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto>();
    }

    public void setStartedProcesses(List<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto> startedProcesses) {
        this.startedProcesses = startedProcesses;
    }

    public List<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto> getKilledProcesses() {
        if (this.killedProcesses != null) {
            return this.killedProcesses;
        }

        return new ArrayList<sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto>();
    }

    public void setKilledProcesses(List<ProcessDto> killedProcesses) {
        this.killedProcesses = killedProcesses;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("processeschangedsincechceck");
    }
}
