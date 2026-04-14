package com.analisys.gimnasio.clases_service.service;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.model.Inscripcion;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import com.analisys.gimnasio.clases_service.repository.InscripcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InscripcionService {

    private static final Logger log = LoggerFactory.getLogger(InscripcionService.class);

    private final InscripcionRepository inscripcionRepository;
    private final ClaseRepository claseRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository, ClaseRepository claseRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.claseRepository = claseRepository;
    }

    @Transactional
    public Inscripcion inscribir(Long miembroId, Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada con ID: " + claseId));

        if (inscripcionRepository.existsByMiembroIdAndClaseId(miembroId, claseId)) {
            throw new IllegalArgumentException("El miembro " + miembroId + " ya está inscrito en la clase " + claseId);
        }

        if (clase.getOcupacionActual() >= clase.getCapacidadMaxima()) {
            throw new IllegalArgumentException("La clase " + clase.getNombre() + " está llena");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .miembroId(miembroId)
                .clase(clase)
                .build();

        clase.setOcupacionActual(clase.getOcupacionActual() + 1);
        claseRepository.save(clase);

        log.info("Miembro {} inscrito en clase {} ({})", miembroId, claseId, clase.getNombre());
        return inscripcionRepository.save(inscripcion);
    }

    public List<Clase> obtenerClasesPorMiembro(Long miembroId) {
        return inscripcionRepository.findByMiembroId(miembroId).stream()
                .map(Inscripcion::getClase)
                .toList();
    }

    @Transactional
    public void cancelarInscripcion(Long miembroId, Long claseId) {
        if (!inscripcionRepository.existsByMiembroIdAndClaseId(miembroId, claseId)) {
            throw new IllegalArgumentException("El miembro " + miembroId + " no está inscrito en la clase " + claseId);
        }

        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada con ID: " + claseId));

        inscripcionRepository.deleteByMiembroIdAndClaseId(miembroId, claseId);

        if (clase.getOcupacionActual() > 0) {
            clase.setOcupacionActual(clase.getOcupacionActual() - 1);
            claseRepository.save(clase);
        }

        log.info("Miembro {} desinscrito de clase {} ({})", miembroId, claseId, clase.getNombre());
    }
}
