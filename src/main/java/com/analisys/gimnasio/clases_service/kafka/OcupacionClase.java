package com.analisys.gimnasio.clases_service.kafka;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionClase {
    private String claseId;
    private int ocupacionActual;
    private LocalDateTime timestamp;
}
