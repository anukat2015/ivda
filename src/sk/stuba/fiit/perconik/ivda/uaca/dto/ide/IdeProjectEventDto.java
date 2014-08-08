package sk.stuba.fiit.perconik.ivda.uaca.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeProjectEventDto extends IdeEventDto {
    public IdeProjectEventDto() {
    }

    public void setEventType(String eventType) {
        setEventTypeUri(UriBuilder.fromUri(getEventTypeUri()).path(eventType).build());
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("project");
    }
}