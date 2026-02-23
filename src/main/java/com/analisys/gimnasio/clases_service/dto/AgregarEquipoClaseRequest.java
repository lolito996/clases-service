package com.analisys.gimnasio.clases_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarEquipoClaseRequest {
    
    @NotNull(message = "El ID del equipo es obligatorio")
    private Long equipoId;
    
    @NotNull(message = "La cantidad requerida es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidadRequerida;
}
