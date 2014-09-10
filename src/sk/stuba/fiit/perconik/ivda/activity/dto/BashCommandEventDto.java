package sk.stuba.fiit.perconik.ivda.activity.dto;

import sk.stuba.fiit.perconik.uaca.dto.*;

import javax.ws.rs.core.UriBuilder;

public class BashCommandEventDto extends sk.stuba.fiit.perconik.uaca.dto.ApplicationEventDto {
    private String commandLine;
    private int commandId;

    public BashCommandEventDto() {
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("bash/command");
    }
}