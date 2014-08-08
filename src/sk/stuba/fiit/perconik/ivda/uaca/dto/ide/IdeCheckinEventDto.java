package sk.stuba.fiit.perconik.ivda.uaca.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeCheckinEventDto extends IdeEventDto {
    /**
     * Changeset id as specified in a RCS
     */
    private String changesetIdInRcs;

    /**
     * Target rcs server or remote repository
     */
    private RcsServerDto rcsServer;

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
    public RcsServerDto getRcsServer() {
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