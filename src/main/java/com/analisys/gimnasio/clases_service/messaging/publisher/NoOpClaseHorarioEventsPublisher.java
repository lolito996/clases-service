package com.analisys.gimnasio.clases_service.messaging.publisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "false")
public class NoOpClaseHorarioEventsPublisher implements ClaseHorarioEventsPublisher {
    @Override
    public void publishHorarioCambiado(Long claseId, LocalDateTime horarioAnterior, LocalDateTime horarioNuevo) {
    }
}
