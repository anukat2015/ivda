package sk.stuba.fiit.perconik.ivda.dto;

import javax.ws.rs.core.UriBuilder;

public class SystemEventDto extends EventDto {
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("system");
	}
}