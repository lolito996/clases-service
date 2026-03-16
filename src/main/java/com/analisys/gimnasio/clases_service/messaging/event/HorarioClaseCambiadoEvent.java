package com.analisys.gimnasio.clases_service.messaging.event;

import java.time.Instant;
import java.time.LocalDateTime;

public class HorarioClaseCambiadoEvent {

    private Long claseId;
    private LocalDateTime horarioAnterior;
    private LocalDateTime horarioNuevo;
    private Instant occurredAt;

    public HorarioClaseCambiadoEvent() {
    }

    public HorarioClaseCambiadoEvent(Long claseId, LocalDateTime horarioAnterior, LocalDateTime horarioNuevo, Instant occurredAt) {
        this.claseId = claseId;
        this.horarioAnterior = horarioAnterior;
        this.horarioNuevo = horarioNuevo;
        this.occurredAt = occurredAt;
    }

    public Long getClaseId() {
        return claseId;
    }

    public LocalDateTime getHorarioAnterior() {
        return horarioAnterior;
    }

    public LocalDateTime getHorarioNuevo() {
        return horarioNuevo;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
