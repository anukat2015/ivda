package sk.stuba.fiit.perconik.ivda.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeCheckinEventRequest extends IdeEventRequest {
    /**
     * Changeset id as specified in a RCS
     */
    private String changesetIdInRcs;

    /**
     * Target rcs server or remote repository
     */
    private RcsServerDto rcsServer;

    public IdeCheckinEventRequest() {
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