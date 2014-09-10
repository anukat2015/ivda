package sk.stuba.fiit.perconik.ivda.activity.dto.ide;


import javax.ws.rs.core.UriBuilder;

public class IdeProjectEventDto extends IdeEventDto {

    private static final long serialVersionUID = 4711138246462652338L;

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("project");
    }
}