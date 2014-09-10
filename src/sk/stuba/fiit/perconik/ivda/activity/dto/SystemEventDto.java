package sk.stuba.fiit.perconik.ivda.activity.dto;

import javax.ws.rs.core.UriBuilder;

public class SystemEventDto extends EventDto {
    private static final long serialVersionUID = -2196950013163342189L;

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("system");
    }
}