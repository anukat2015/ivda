package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeDocumentEventDto extends IdeEventDto {
    private static final long serialVersionUID = -1950941763338557186L;
    /**
     * Document that has been subject of this event
     */

    private IdeDocumentDto document;

    /**
     * @return the {@link #document}
     */
    public IdeDocumentDto getDocument() {
        return document;
    }

    /**
     * @param {@link #document}
     */
    public void setDocument(IdeDocumentDto document) {
        this.document = document;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("document");
    }
}