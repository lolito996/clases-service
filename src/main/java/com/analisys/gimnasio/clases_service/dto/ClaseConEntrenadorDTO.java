package com.analisys.gimnasio.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO con información completa de la clase incluyendo entrenador
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaseConEntrenadorDTO {
    private Long id;
    private String nombre;
    private LocalDateTime horario;
    private int capacidadMaxima;
    private Long entrenadorId;
    private TrainerResponseDTO entrenador;
}
