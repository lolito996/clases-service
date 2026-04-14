package com.analisys.gimnasio.clases_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inscripcion", uniqueConstraints = @UniqueConstraint(columnNames = {"miembro_id", "clase_id"}))
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "miembro_id", nullable = false)
    private Long miembroId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase;

    @Column(nullable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    protected void onCreate() {
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
    }
}
