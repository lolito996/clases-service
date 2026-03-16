package com.analisys.gimnasio.clases_service.messaging.publisher;

public interface ClaseTrainerEventsPublisher {
    void publishEntrenadorAsignado(Long claseId, Long entrenadorId);
}
