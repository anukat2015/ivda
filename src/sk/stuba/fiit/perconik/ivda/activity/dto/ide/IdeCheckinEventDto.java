package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import sk.stuba.fiit.perconik.uaca.dto.ide.*;
import sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto;

import javax.ws.rs.core.UriBuilder;

public class IdeCheckinEventDto extends sk.stuba.fiit.perconik.uaca.dto.ide.IdeEventDto {
    /**
     * Changeset id as specified in a RCS
     */
    private String changesetIdInRcs;

    /**
     * Target rcs server or remote repository
     */
    private sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto rcsServer;

    public IdeCheckinEventDto() {
    }

    /**
     * @return the {@link #changesetIdInRcs}
     */
    public String getChangesetIdInRcs() {
        return changesetIdInRcs;
    }

    /**
     * @param {@link #changesetIdInRcs}
     */
    public void setChangesetIdInRcs(String changesetIdInRcs) {
        this.changesetIdInRcs = changesetIdInRcs;
    }

    /**
     * @return the {@link #rcsServer}
     */
    public sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto getRcsServer() {
        return rcsServer;
    }

    /**
     * @param {@link #rcsServer}
     */
    public void setRcsServer(RcsServerDto rcsServer) {
        this.rcsServer = rcsServer;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("checkin");
    }
}