package sk.stuba.fiit.perconik.ivda.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeDocumentEventRequest extends IdeEventRequest {
    /**
     * Document that has been subject of this event
     */

    private IdeDocumentDto document;

    public IdeDocumentEventRequest() {
    }

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

    public void setEventType(String eventType) {
        setEventTypeUri(UriBuilder.fromUri(getEventTypeUri()).path(eventType).build());
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("document");
    }
}