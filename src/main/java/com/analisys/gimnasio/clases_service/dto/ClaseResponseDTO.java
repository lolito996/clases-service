package com.analisys.gimnasio.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaseResponseDTO {
    
    private Long id;
    private String nombre;
    private LocalDateTime horario;
    private int capacidadMaxima;
    private int ocupacionActual;
    private List<ClaseEquipoDTO> equiposRequeridos;
    private Boolean equiposDisponibles;
}
