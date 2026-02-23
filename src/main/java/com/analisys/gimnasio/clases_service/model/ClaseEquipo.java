package com.analisys.gimnasio.clases_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la relación entre una clase y los equipos que requiere.
 * Almacena el ID del equipo (referencia al servicio de equipos) y la cantidad necesaria.
 */
@Entity
@Table(name = "clase_equipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaseEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase;

    @Column(nullable = false)
    private Long equipoId;

    @Column(nullable = false)
    private Integer cantidadRequerida;

    private String equipoNombre;

    public ClaseEquipo(Clase clase, Long equipoId, Integer cantidadRequerida) {
        this.clase = clase;
        this.equipoId = equipoId;
        this.cantidadRequerida = cantidadRequerida;
    }
}