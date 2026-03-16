package com.analisys.gimnasio.clases_service.messaging.publisher;

import com.analisys.gimnasio.clases_service.messaging.event.HorarioClaseCambiadoEvent;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitClaseHorarioEventsPublisher implements ClaseHorarioEventsPublisher {

    public static final String ROUTING_KEY = "clase.horario.cambiado";

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange gimnasioExchange;

    public RabbitClaseHorarioEventsPublisher(RabbitTemplate rabbitTemplate, TopicExchange gimnasioExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.gimnasioExchange = gimnasioExchange;
    }

    @Override
    public void publishHorarioCambiado(Long claseId, LocalDateTime horarioAnterior, LocalDateTime horarioNuevo) {
        HorarioClaseCambiadoEvent event = new HorarioClaseCambiadoEvent(claseId, horarioAnterior, horarioNuevo, Instant.now());
        Runnable send = () -> rabbitTemplate.convertAndSend(gimnasioExchange.getName(), ROUTING_KEY, event);

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
