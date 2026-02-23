package com.analisys.gimnasio.clases_service.service;

import com.analisys.gimnasio.clases_service.client.TrainerServiceClient;
import com.analisys.gimnasio.clases_service.dto.ClaseConEntrenadorDTO;
import com.analisys.gimnasio.clases_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.clases_service.exception.ClaseNotFoundException;
import com.analisys.gimnasio.clases_service.exception.TrainerServiceException;
import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaseTrainerService {

    private static final Logger log = LoggerFactory.getLogger(ClaseTrainerService.class);

    private final ClaseRepository claseRepository;
    private final TrainerServiceClient trainerServiceClient;

    public ClaseTrainerService(ClaseRepository claseRepository, TrainerServiceClient trainerServiceClient) {
        this.claseRepository = claseRepository;
        this.trainerServiceClient = trainerServiceClient;
    }

    // asignar un entrenador a una clase
    @Transactional
    public ClaseConEntrenadorDTO asignarEntrenador(Long claseId, Long entrenadorId) {
        log.info("Asignando entrenador {} a clase {}", entrenadorId, claseId);

        // Verificar que la clase existe
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException("Clase no encontrada con ID: " + claseId));

        // Verificar que el entrenador existe (validación contra servicio externo)
        TrainerResponseDTO entrenador = trainerServiceClient.getTrainerById(entrenadorId);
        
        // Asignar el entrenador
        clase.setEntrenadorId(entrenadorId);
        Clase claseActualizada = claseRepository.save(clase);

        log.info("Entrenador {} ({}) asignado a clase {} ({})", 
                entrenador.getNombre(), entrenadorId, clase.getNombre(), claseId);

        return buildClaseConEntrenadorDTO(claseActualizada, entrenador);
    }

    // obtiener el detalle de una clase con información del entrenador
    public ClaseConEntrenadorDTO obtenerClaseConEntrenador(Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException("Clase no encontrada con ID: " + claseId));

        TrainerResponseDTO entrenador = null;
        if (clase.getEntrenadorId() != null) {
            try {
                entrenador = trainerServiceClient.getTrainerById(clase.getEntrenadorId());
            } catch (TrainerServiceException e) {
                log.warn("No se pudo obtener info del entrenador {}: {}", clase.getEntrenadorId(), e.getMessage());
            }
        }

        return buildClaseConEntrenadorDTO(clase, entrenador);
    }

    // listar todas las clases con información de entrenadores
    public List<ClaseConEntrenadorDTO> listarClasesConEntrenadores() {
        return claseRepository.findAll().stream()
                .map(clase -> {
                    TrainerResponseDTO entrenador = null;
                    if (clase.getEntrenadorId() != null) {
                        try {
                            entrenador = trainerServiceClient.getTrainerById(clase.getEntrenadorId());
                        } catch (TrainerServiceException e) {
                            log.warn("No se pudo obtener entrenador {}", clase.getEntrenadorId());
                        }
                    }
                    return buildClaseConEntrenadorDTO(clase, entrenador);
                })
                .toList();
    }

    // buscar clases por especialidad del entrenador
    public List<ClaseConEntrenadorDTO> buscarClasesPorEspecialidad(String especialidad) {
        List<TrainerResponseDTO> entrenadores = trainerServiceClient.getTrainersBySpecialty(especialidad);
        List<Long> entrenadorIds = entrenadores.stream().map(TrainerResponseDTO::getId).toList();

        return claseRepository.findAll().stream()
                .filter(clase -> clase.getEntrenadorId() != null && entrenadorIds.contains(clase.getEntrenadorId()))
                .map(clase -> {
                    TrainerResponseDTO entrenador = entrenadores.stream()
                            .filter(e -> e.getId().equals(clase.getEntrenadorId()))
                            .findFirst().orElse(null);
                    return buildClaseConEntrenadorDTO(clase, entrenador);
                })
                .toList();
    }

    // quitar entrenador de una clase
    @Transactional
    public Clase removerEntrenador(Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException("Clase no encontrada con ID: " + claseId));

        log.info("Removiendo entrenador {} de clase {}", clase.getEntrenadorId(), claseId);
        clase.setEntrenadorId(null);
        return claseRepository.save(clase);
    }

    // todos los entrenadores disponibles
    public List<TrainerResponseDTO> listarEntrenadoresDisponibles() {
        return trainerServiceClient.getAllTrainers();
    }

    private ClaseConEntrenadorDTO buildClaseConEntrenadorDTO(Clase clase, TrainerResponseDTO entrenador) {
        return ClaseConEntrenadorDTO.builder()
                .id(clase.getId())
                .nombre(clase.getNombre())
                .horario(clase.getHorario())
                .capacidadMaxima(clase.getCapacidadMaxima())
                .entrenadorId(clase.getEntrenadorId())
                .entrenador(entrenador)
                .build();
    }
}
