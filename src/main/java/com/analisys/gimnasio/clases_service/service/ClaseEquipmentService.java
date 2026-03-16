package com.analisys.gimnasio.clases_service.service;

import com.analisys.gimnasio.clases_service.client.EquipmentServiceClient;
import com.analisys.gimnasio.clases_service.dto.ClaseEquipoDTO;
import com.analisys.gimnasio.clases_service.dto.ClaseResponseDTO;
import com.analisys.gimnasio.clases_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.clases_service.exception.ClaseNotFoundException;
import com.analisys.gimnasio.clases_service.exception.EquipmentServiceException;
import com.analisys.gimnasio.clases_service.exception.EquiposNoDisponiblesException;
import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.model.ClaseEquipo;
import com.analisys.gimnasio.clases_service.repository.ClaseEquipoRepository;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que orquesta la integración entre clases y el servicio de equipos.
 * Implementa la lógica de negocio para:
 * - Agregar equipos requeridos a una clase
 * - Verificar disponibilidad de equipos para una clase
 * - Reservar todos los equipos necesarios para iniciar una clase
 * - Liberar equipos cuando termina una clase
 */
@Service
public class ClaseEquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(ClaseEquipmentService.class);

    private final ClaseRepository claseRepository;
    private final ClaseEquipoRepository claseEquipoRepository;
    private final EquipmentServiceClient equipmentClient;

    public ClaseEquipmentService(ClaseRepository claseRepository,
                                  ClaseEquipoRepository claseEquipoRepository,
                                  EquipmentServiceClient equipmentClient) {
        this.claseRepository = claseRepository;
        this.claseEquipoRepository = claseEquipoRepository;
        this.equipmentClient = equipmentClient;
    }

    /**
     * Agrega un equipo requerido a una clase.
     *
     * @param claseId ID de la clase
     * @param equipoId ID del equipo a agregar
     * @param cantidadRequerida Cantidad de unidades del equipo requeridas
     * @return DTO con la información del equipo agregado
     */
    @Transactional
    public ClaseEquipoDTO agregarEquipoAClase(Long claseId, Long equipoId, int cantidadRequerida) {
        logger.info("Agregando equipo {} a clase {} con cantidad {}", equipoId, claseId, cantidadRequerida);
        
        // Verificar que la clase existe
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new ClaseNotFoundException(claseId));
        
        // Verificar que el equipo existe en el servicio de equipos
        EquipmentResponseDTO equipo = equipmentClient.getEquipmentById(equipoId)
            .orElseThrow(() -> new EquipmentServiceException("Equipo no encontrado con ID: " + equipoId));
        
        // Verificar si ya existe la relación
        if (claseEquipoRepository.existsByClaseIdAndEquipoId(claseId, equipoId)) {
            throw new IllegalArgumentException(
                "El equipo " + equipo.getNombre() + " ya está asignado a esta clase");
        }
        
        // Crear la relación
        ClaseEquipo claseEquipo = new ClaseEquipo(clase, equipoId, cantidadRequerida);
        claseEquipoRepository.save(claseEquipo);
        
        logger.info("Equipo {} agregado exitosamente a clase {}", equipo.getNombre(), clase.getNombre());
        
        // Retornar DTO con información del equipo
        return new ClaseEquipoDTO(
            equipoId,
            equipo.getNombre(),
            cantidadRequerida,
            equipo.getCantidadDisponible(),
            equipo.getCantidadDisponible() >= cantidadRequerida && "DISPONIBLE".equals(equipo.getEstado())
        );
    }

    /**
     * Obtiene los equipos requeridos por una clase con su disponibilidad actual.
     *
     * @param claseId ID de la clase
     * @return Lista de DTOs con información de cada equipo requerido
     */
    public List<ClaseEquipoDTO> obtenerEquiposDeClase(Long claseId) {
        logger.info("Obteniendo equipos de clase {}", claseId);
        
        // Verificar que la clase existe
        if (!claseRepository.existsById(claseId)) {
            throw new ClaseNotFoundException(claseId);
        }
        
        List<ClaseEquipo> equiposRequeridos = claseEquipoRepository.findByClaseId(claseId);
        List<ClaseEquipoDTO> resultado = new ArrayList<>();
        
        for (ClaseEquipo ce : equiposRequeridos) {
            try {
                Optional<EquipmentResponseDTO> equipoOpt = equipmentClient.getEquipmentById(ce.getEquipoId());
                
                if (equipoOpt.isPresent()) {
                    EquipmentResponseDTO equipo = equipoOpt.get();
                    resultado.add(new ClaseEquipoDTO(
                        ce.getEquipoId(),
                        equipo.getNombre(),
                        ce.getCantidadRequerida(),
                        equipo.getCantidadDisponible(),
                        equipo.getCantidadDisponible() >= ce.getCantidadRequerida() && 
                            "DISPONIBLE".equals(equipo.getEstado())
                    ));
                } else {
                    // El equipo ya no existe, agregar con información parcial
                    resultado.add(new ClaseEquipoDTO(
                        ce.getEquipoId(),
                        "Equipo no encontrado",
                        ce.getCantidadRequerida(),
                        0,
                        false
                    ));
                }
            } catch (EquipmentServiceException e) {
                logger.warn("Error al consultar equipo {}: {}", ce.getEquipoId(), e.getMessage());
                resultado.add(new ClaseEquipoDTO(
                    ce.getEquipoId(),
                    "Error al consultar",
                    ce.getCantidadRequerida(),
                    0,
                    false
                ));
            }
        }
        
        return resultado;
    }

    /**
     * Obtiene información detallada de una clase incluyendo equipos requeridos.
     *
     * @param claseId ID de la clase
     * @return DTO con toda la información de la clase
     */
    public ClaseResponseDTO obtenerClaseConEquipos(Long claseId) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new ClaseNotFoundException(claseId));
        
        List<ClaseEquipoDTO> equipos = obtenerEquiposDeClase(claseId);
        boolean todosDisponibles = equipos.stream().allMatch(ClaseEquipoDTO::getDisponible);
        
        return new ClaseResponseDTO(
            clase.getId(),
            clase.getNombre(),
            clase.getHorario(),
            clase.getCapacidadMaxima(),
            clase.getOcupacionActual(),
            equipos,
            todosDisponibles
        );
    }

    /**
     * Reserva todos los equipos necesarios para iniciar una clase.
     * Implementa la lógica de "todo o nada": si algún equipo no está disponible,
     * no se reserva ninguno.
     *
     * @param claseId ID de la clase
     * @return DTO con el estado de la clase y equipos reservados
     */
    @Transactional
    public ClaseResponseDTO reservarEquiposParaClase(Long claseId) {
        logger.info("Reservando equipos para clase {}", claseId);
        
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new ClaseNotFoundException(claseId));
        
        List<ClaseEquipo> equiposRequeridos = claseEquipoRepository.findByClaseId(claseId);
        
        if (equiposRequeridos.isEmpty()) {
            throw new IllegalArgumentException("La clase no tiene equipos requeridos asignados");
        }
        
        // Primero verificar disponibilidad de todos los equipos
        List<String> equiposNoDisponibles = new ArrayList<>();
        for (ClaseEquipo ce : equiposRequeridos) {
            if (!equipmentClient.isEquipmentAvailable(ce.getEquipoId(), ce.getCantidadRequerida())) {
                Optional<EquipmentResponseDTO> equipo = equipmentClient.getEquipmentById(ce.getEquipoId());
                String nombreEquipo = equipo.map(EquipmentResponseDTO::getNombre).orElse("ID: " + ce.getEquipoId());
                equiposNoDisponibles.add(nombreEquipo + " (necesita: " + ce.getCantidadRequerida() + ")");
            }
        }
        
        if (!equiposNoDisponibles.isEmpty()) {
            throw new EquiposNoDisponiblesException(
                "No se pueden reservar los equipos. No disponibles: " + String.join(", ", equiposNoDisponibles));
        }
        
        // Reservar todos los equipos
        List<ClaseEquipoDTO> equiposReservados = new ArrayList<>();
        List<Long> equiposYaReservados = new ArrayList<>(); // Para rollback si falla
        
        try {
            for (ClaseEquipo ce : equiposRequeridos) {
                EquipmentResponseDTO equipoActualizado = equipmentClient.useEquipment(
                    ce.getEquipoId(), 
                    ce.getCantidadRequerida()
                );
                
                equiposYaReservados.add(ce.getEquipoId());
                equiposReservados.add(new ClaseEquipoDTO(
                    ce.getEquipoId(),
                    equipoActualizado.getNombre(),
                    ce.getCantidadRequerida(),
                    equipoActualizado.getCantidadDisponible(),
                    true // Ya está reservado
                ));
            }
            
            logger.info("Todos los equipos reservados para clase {}", clase.getNombre());
            
            return new ClaseResponseDTO(
                clase.getId(),
                clase.getNombre(),
                clase.getHorario(),
                clase.getCapacidadMaxima(),
                clase.getOcupacionActual(),
                equiposReservados,
                true
            );
            
        } catch (Exception e) {
            // Rollback: liberar los equipos ya reservados
            logger.error("Error al reservar equipos, haciendo rollback: {}", e.getMessage());
            
            for (int i = 0; i < equiposYaReservados.size(); i++) {
                try {
                    Long equipoId = equiposYaReservados.get(i);
                    int cantidad = equiposRequeridos.stream()
                        .filter(ce -> ce.getEquipoId().equals(equipoId))
                        .findFirst()
                        .map(ClaseEquipo::getCantidadRequerida)
                        .orElse(0);
                    
                    if (cantidad > 0) {
                        equipmentClient.releaseEquipment(equipoId, cantidad);
                        logger.info("Rollback: liberado equipo {}", equipoId);
                    }
                } catch (Exception rollbackEx) {
                    logger.error("Error en rollback para equipo {}: {}", 
                        equiposYaReservados.get(i), rollbackEx.getMessage());
                }
            }
            
            throw new EquipmentServiceException("Error al reservar equipos: " + e.getMessage(), e);
        }
    }

    /**
     * Libera todos los equipos de una clase (cuando termina la clase).
     *
     * @param claseId ID de la clase
     * @return Lista de equipos liberados
     */
    @Transactional
    public List<ClaseEquipoDTO> liberarEquiposDeClase(Long claseId) {
        logger.info("Liberando equipos de clase {}", claseId);
        
        if (!claseRepository.existsById(claseId)) {
            throw new ClaseNotFoundException(claseId);
        }
        
        List<ClaseEquipo> equiposRequeridos = claseEquipoRepository.findByClaseId(claseId);
        List<ClaseEquipoDTO> equiposLiberados = new ArrayList<>();
        
        for (ClaseEquipo ce : equiposRequeridos) {
            try {
                EquipmentResponseDTO equipoActualizado = equipmentClient.releaseEquipment(
                    ce.getEquipoId(),
                    ce.getCantidadRequerida()
                );
                
                equiposLiberados.add(new ClaseEquipoDTO(
                    ce.getEquipoId(),
                    equipoActualizado.getNombre(),
                    ce.getCantidadRequerida(),
                    equipoActualizado.getCantidadDisponible(),
                    true
                ));
                
                logger.info("Equipo {} liberado de clase {}", equipoActualizado.getNombre(), claseId);
                
            } catch (Exception e) {
                logger.error("Error al liberar equipo {}: {}", ce.getEquipoId(), e.getMessage());
                equiposLiberados.add(new ClaseEquipoDTO(
                    ce.getEquipoId(),
                    "Error al liberar",
                    ce.getCantidadRequerida(),
                    0,
                    false
                ));
            }
        }
        
        return equiposLiberados;
    }

    /**
     * Elimina un equipo requerido de una clase.
     *
     * @param claseId ID de la clase
     * @param equipoId ID del equipo a eliminar
     */
    @Transactional
    public void eliminarEquipoDeClase(Long claseId, Long equipoId) {
        logger.info("Eliminando equipo {} de clase {}", equipoId, claseId);
        
        if (!claseRepository.existsById(claseId)) {
            throw new ClaseNotFoundException(claseId);
        }
        
        if (!claseEquipoRepository.existsByClaseIdAndEquipoId(claseId, equipoId)) {
            throw new IllegalArgumentException(
                "El equipo " + equipoId + " no está asignado a la clase " + claseId);
        }
        
        claseEquipoRepository.deleteByClaseIdAndEquipoId(claseId, equipoId);
        logger.info("Equipo {} eliminado de clase {}", equipoId, claseId);
    }

    /**
     * Verifica si todos los equipos de una clase están disponibles.
     *
     * @param claseId ID de la clase
     * @return true si todos los equipos están disponibles
     */
    public boolean verificarDisponibilidadEquipos(Long claseId) {
        List<ClaseEquipoDTO> equipos = obtenerEquiposDeClase(claseId);
        return equipos.stream().allMatch(ClaseEquipoDTO::getDisponible);
    }
}
