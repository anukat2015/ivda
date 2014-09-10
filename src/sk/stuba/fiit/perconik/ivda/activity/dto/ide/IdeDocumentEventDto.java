package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import sk.stuba.fiit.perconik.uaca.dto.ide.*;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto;

import javax.ws.rs.core.UriBuilder;

public class IdeDocumentEventDto extends sk.stuba.fiit.perconik.uaca.dto.ide.IdeEventDto {
    /**
     * Document that has been subject of this event
     */

    private sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto document;

    public IdeDocumentEventDto() {
    }

    /**
     * @return the {@link #document}
     */
    public sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto getDocument() {
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