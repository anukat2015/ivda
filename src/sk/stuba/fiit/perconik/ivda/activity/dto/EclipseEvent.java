package sk.stuba.fiit.perconik.ivda.activity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.ws.rs.core.UriBuilder;

/**
 * Created by Seky on 6. 11. 2014.
 * Like:
 * http://perconik.gratex.com/useractivity/event/eclipse/workbench/startup
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EclipseEvent extends EventDto {
    private static final long serialVersionUID = -2712907215458601626L;

    public EclipseEvent() {
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("eclipse");
    }
}
