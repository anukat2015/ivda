package sk.stuba.fiit.perconik.ivda.activity.dto;

import sk.stuba.fiit.perconik.ivda.activity.dto.*;

import javax.ws.rs.core.UriBuilder;

public class SystemEventDto extends sk.stuba.fiit.perconik.ivda.activity.dto.EventDto {
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("system");
	}
}