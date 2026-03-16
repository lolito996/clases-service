package com.analisys.gimnasio.clases_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OcupacionClaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcupacionClaseConsumer.class);

    @KafkaListener(topics = "ocupacion-clases", groupId = "monitoreo-grupo")
    public void consumirActualizacionOcupacion(OcupacionClase ocupacion) {
        logger.info("Actualizacion de ocupacion recibida: {}", ocupacion);
    }
}
