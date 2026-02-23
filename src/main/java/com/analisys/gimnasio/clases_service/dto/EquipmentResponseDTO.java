package com.analisys.gimnasio.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponseDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidadTotal;
    private Integer cantidadDisponible;
    private String estado;
    private boolean disponible;
}
