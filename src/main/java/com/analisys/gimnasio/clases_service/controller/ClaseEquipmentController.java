package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.dto.AgregarEquipoClaseRequest;
import com.analisys.gimnasio.clases_service.dto.ClaseEquipoDTO;
import com.analisys.gimnasio.clases_service.dto.ClaseResponseDTO;
import com.analisys.gimnasio.clases_service.service.ClaseEquipmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la integración entre clases y equipos.
 * Expone endpoints para gestionar los equipos requeridos por las clases.
 */
@RestController
@RequestMapping("/gym/clases")
public class ClaseEquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(ClaseEquipmentController.class);

    private final ClaseEquipmentService claseEquipmentService;

    public ClaseEquipmentController(ClaseEquipmentService claseEquipmentService) {
        this.claseEquipmentService = claseEquipmentService;
    }

    /**
     * Agrega un equipo requerido a una clase.
     * 
     * POST /gym/clases/{claseId}/equipos
     * Body: { "equipoId": 1, "cantidadRequerida": 5 }
     */
    @PostMapping("/{claseId}/equipos")
    public ResponseEntity<ClaseEquipoDTO> agregarEquipoAClase(
            @PathVariable Long claseId,
            @Valid @RequestBody AgregarEquipoClaseRequest request) {
        
        logger.info("POST /gym/clases/{}/equipos - Agregando equipo {}", claseId, request.getEquipoId());
        
        ClaseEquipoDTO resultado = claseEquipmentService.agregarEquipoAClase(
            claseId, 
            request.getEquipoId(), 
            request.getCantidadRequerida()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Obtiene los equipos requeridos por una clase con su disponibilidad.
     * 
     * GET /gym/clases/{claseId}/equipos
     */
    @GetMapping("/{claseId}/equipos")
    public ResponseEntity<List<ClaseEquipoDTO>> obtenerEquiposDeClase(@PathVariable Long claseId) {
        logger.info("GET /gym/clases/{}/equipos", claseId);
        
        List<ClaseEquipoDTO> equipos = claseEquipmentService.obtenerEquiposDeClase(claseId);
        return ResponseEntity.ok(equipos);
    }

    /**
     * Obtiene información detallada de una clase incluyendo equipos.
     * 
     * GET /gym/clases/{claseId}/detalle
     */
    @GetMapping("/{claseId}/detalle")
    public ResponseEntity<ClaseResponseDTO> obtenerClaseConEquipos(@PathVariable Long claseId) {
        logger.info("GET /gym/clases/{}/detalle", claseId);
        
        ClaseResponseDTO clase = claseEquipmentService.obtenerClaseConEquipos(claseId);
        return ResponseEntity.ok(clase);
    }

    /**
     * Reserva todos los equipos necesarios para iniciar una clase.
     * si algún equipo no está disponible, no reserva ninguno.
     * 
     * POST /gym/clases/{claseId}/reservar-equipos
     */
    @PostMapping("/{claseId}/reservar-equipos")
    public ResponseEntity<ClaseResponseDTO> reservarEquiposParaClase(@PathVariable Long claseId) {
        logger.info("POST /gym/clases/{}/reservar-equipos", claseId);
        
        ClaseResponseDTO resultado = claseEquipmentService.reservarEquiposParaClase(claseId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Libera todos los equipos de una clase (cuando termina la clase).
     * 
     * POST /gym/clases/{claseId}/liberar-equipos
     */
    @PostMapping("/{claseId}/liberar-equipos")
    public ResponseEntity<Map<String, Object>> liberarEquiposDeClase(@PathVariable Long claseId) {
        logger.info("POST /gym/clases/{}/liberar-equipos", claseId);
        
        List<ClaseEquipoDTO> equiposLiberados = claseEquipmentService.liberarEquiposDeClase(claseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Equipos liberados exitosamente");
        response.put("claseId", claseId);
        response.put("equiposLiberados", equiposLiberados);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si todos los equipos de una clase están disponibles.
     * 
     * GET /gym/clases/{claseId}/verificar-disponibilidad
     */
    @GetMapping("/{claseId}/verificar-disponibilidad")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(@PathVariable Long claseId) {
        logger.info("GET /gym/clases/{}/verificar-disponibilidad", claseId);
        
        boolean disponible = claseEquipmentService.verificarDisponibilidadEquipos(claseId);
        List<ClaseEquipoDTO> equipos = claseEquipmentService.obtenerEquiposDeClase(claseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("claseId", claseId);
        response.put("todosDisponibles", disponible);
        response.put("equipos", equipos);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un equipo requerido de una clase.
     * 
     * DELETE /gym/clases/{claseId}/equipos/{equipoId}
     */
    @DeleteMapping("/{claseId}/equipos/{equipoId}")
    public ResponseEntity<Map<String, String>> eliminarEquipoDeClase(
            @PathVariable Long claseId,
            @PathVariable Long equipoId) {
        
        logger.info("DELETE /gym/clases/{}/equipos/{}", claseId, equipoId);
        
        claseEquipmentService.eliminarEquipoDeClase(claseId, equipoId);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Equipo eliminado de la clase exitosamente");
        
        return ResponseEntity.ok(response);
    }
}
