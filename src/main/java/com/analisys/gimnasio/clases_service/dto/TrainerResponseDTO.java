package com.analisys.gimnasio.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponseDTO {
    private Long id;
    private String nombre;
    private String especialidad;
}
