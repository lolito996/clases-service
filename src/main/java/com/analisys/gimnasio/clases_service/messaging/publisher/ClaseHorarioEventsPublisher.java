package com.analisys.gimnasio.clases_service.messaging.publisher;

import java.time.LocalDateTime;

public interface ClaseHorarioEventsPublisher {
    void publishHorarioCambiado(Long claseId, LocalDateTime horarioAnterior, LocalDateTime horarioNuevo);
}
