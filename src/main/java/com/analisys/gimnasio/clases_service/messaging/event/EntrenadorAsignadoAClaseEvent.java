package com.analisys.gimnasio.clases_service.messaging.event;

import java.time.Instant;

public class EntrenadorAsignadoAClaseEvent {

	private Long claseId;
	private Long entrenadorId;
	private Instant occurredAt;

	public EntrenadorAsignadoAClaseEvent() {
	}

	public EntrenadorAsignadoAClaseEvent(Long claseId, Long entrenadorId, Instant occurredAt) {
		this.claseId = claseId;
		this.entrenadorId = entrenadorId;
		this.occurredAt = occurredAt;
	}

	public Long getClaseId() {
		return claseId;
	}

	public Long getEntrenadorId() {
		return entrenadorId;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}
}
