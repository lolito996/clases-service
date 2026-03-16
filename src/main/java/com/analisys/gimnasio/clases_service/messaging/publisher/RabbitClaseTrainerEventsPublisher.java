package com.analisys.gimnasio.clases_service.messaging.publisher;

import java.time.Instant;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.analisys.gimnasio.clases_service.messaging.event.EntrenadorAsignadoAClaseEvent;
/*

* Esta clase es un servicio de Spring que se encarga de publicar eventos relacionados con la asignación de entrenadores a clases en el gimnasio.
* Utiliza RabbitMQ para enviar mensajes a una cola específica cuando un entrenador es asignado a
* una clase. La publicación del evento se realiza solo después de que la transacción actual se haya comprometido exitosamente, asegurando así la consistencia de los datos.
*/

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitClaseTrainerEventsPublisher implements ClaseTrainerEventsPublisher {

    // Definimos la routing key para el evento de entrenador asignado a clase
    public static final String ROUTING_KEY = "clase.entrenador.asignado";
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange gimnasioExchange;
    // Constructor para inyección de dependencias
    public RabbitClaseTrainerEventsPublisher(RabbitTemplate rabbitTemplate, TopicExchange gimnasioExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.gimnasioExchange = gimnasioExchange;
    }
    // Método para publicar el evento de entrenador asignado a clase
    @Override
    public void publishEntrenadorAsignado(Long claseId, Long entrenadorId){
        // Creamos el evento con la información relevante
        EntrenadorAsignadoAClaseEvent event = new EntrenadorAsignadoAClaseEvent(claseId, entrenadorId, Instant.now());
        // Definimos la tarea que se ejecutará para publicar el evento    
        Runnable send = () -> rabbitTemplate.convertAndSend(gimnasioExchange.getName(), ROUTING_KEY, event);

        // Aseguramos que el evento se publique solo después de que la transacción actual se haya comprometido
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        } else {
            send.run();
        }

    }
    
}
