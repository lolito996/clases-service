package com.analisys.gimnasio.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaseEquipoDTO {
    
    private Long equipoId;
    private String equipoNombre;
    private Integer cantidadRequerida;
    private Integer cantidadDisponible;
    private Boolean disponible;
}
