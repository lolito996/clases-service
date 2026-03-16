package com.analisys.gimnasio.clases_service.kafka;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OcupacionClaseProducer {

    private final KafkaTemplate<String, OcupacionClase> kafkaTemplate;

    public OcupacionClaseProducer(KafkaTemplate<String, OcupacionClase> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void actualizarOcupacion(String claseId, int ocupacionActual) {
        OcupacionClase ocupacion = new OcupacionClase(claseId, ocupacionActual, LocalDateTime.now());
        kafkaTemplate.send("ocupacion-clases", claseId, ocupacion);
    }
}
