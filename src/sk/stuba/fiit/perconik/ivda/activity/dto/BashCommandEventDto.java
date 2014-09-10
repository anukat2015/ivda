package sk.stuba.fiit.perconik.ivda.activity.dto;

import javax.ws.rs.core.UriBuilder;

public class BashCommandEventDto extends ApplicationEventDto {
    private static final long serialVersionUID = -2729468962973458909L;
    private String commandLine;
    private int commandId;

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