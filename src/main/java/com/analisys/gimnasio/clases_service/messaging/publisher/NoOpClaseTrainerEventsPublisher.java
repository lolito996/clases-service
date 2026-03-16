package com.analisys.gimnasio.clases_service.messaging.publisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "false")
public class NoOpClaseTrainerEventsPublisher implements ClaseTrainerEventsPublisher {
    @Override
    public void publishEntrenadorAsignado(Long claseId, Long entrenadorId) { }
}