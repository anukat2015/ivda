package sk.stuba.fiit.perconik.ivda.activity.dto;

import sk.stuba.fiit.perconik.uaca.dto.*;

import javax.ws.rs.core.UriBuilder;

public class SystemEventDto extends sk.stuba.fiit.perconik.uaca.dto.EventDto {
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("system");
	}
}