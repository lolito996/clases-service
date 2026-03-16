package com.analisys.gimnasio.clases_service.service;

import com.analisys.gimnasio.clases_service.kafka.OcupacionClaseProducer;
import com.analisys.gimnasio.clases_service.messaging.publisher.ClaseHorarioEventsPublisher;
import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaseService {
    
    private final ClaseRepository claseRepository;
    private final OcupacionClaseProducer ocupacionClaseProducer;
    private final ClaseHorarioEventsPublisher claseHorarioEventsPublisher;

    public ClaseService(
            ClaseRepository claseRepository,
            OcupacionClaseProducer ocupacionClaseProducer,
            ClaseHorarioEventsPublisher claseHorarioEventsPublisher
    ) {
        this.claseRepository = claseRepository;
        this.ocupacionClaseProducer = ocupacionClaseProducer;
        this.claseHorarioEventsPublisher = claseHorarioEventsPublisher;
    }

    public Clase crearClase(Clase clase) {
        // Validar que no se esté especificando un ID
        if (clase.getId() != null) {
            throw new IllegalArgumentException("No se puede especificar un ID al crear una nueva clase. El ID se genera automáticamente.");
        }
        return claseRepository.save(clase);
    }

    public List<Clase> obtenerTodasLasClases() {
        return claseRepository.findAll();
    }

    public void eliminarClase(Long id) {
        claseRepository.deleteById(id);
    }

    public List<Clase> leerDatosIniciales() {
        return claseRepository.findAll();
    }

    public Clase actualizarOcupacion(Long claseId, int ocupacionActual) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada con ID: " + claseId));

        if (ocupacionActual < 0 || ocupacionActual > clase.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                "Ocupacion invalida. Debe estar entre 0 y " + clase.getCapacidadMaxima());
        }

        clase.setOcupacionActual(ocupacionActual);
        Clase actualizada = claseRepository.save(clase);
        ocupacionClaseProducer.actualizarOcupacion(String.valueOf(claseId), ocupacionActual);
        return actualizada;
    }

    public Clase actualizarHorario(Long claseId, LocalDateTime nuevoHorario) {
        if (nuevoHorario == null) {
            throw new IllegalArgumentException("El horario no puede ser null");
        }

        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada con ID: " + claseId));

        LocalDateTime horarioAnterior = clase.getHorario();
        clase.setHorario(nuevoHorario);
        Clase actualizada = claseRepository.save(clase);

        claseHorarioEventsPublisher.publishHorarioCambiado(claseId, horarioAnterior, nuevoHorario);
        return actualizada;
    }
}
