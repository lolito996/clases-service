package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.dto.AgregarEquipoClaseRequest;
import com.analisys.gimnasio.clases_service.dto.ClaseEquipoDTO;
import com.analisys.gimnasio.clases_service.dto.ClaseResponseDTO;
import com.analisys.gimnasio.clases_service.service.ClaseEquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clases - Equipos", description = "Integración de clases con equipos (reserva, liberación y disponibilidad)")
@SecurityRequirement(name = "bearer-jwt")
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
    @Operation(summary = "Agregar equipo a una clase",
        description = "Agrega un equipo requerido a una clase indicando la cantidad requerida.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Equipo agregado a la clase"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Clase o equipo no encontrado"),
        @ApiResponse(responseCode = "503", description = "Servicio de equipos no disponible")
    })
    public ResponseEntity<ClaseEquipoDTO> agregarEquipoAClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId,
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
    @Operation(summary = "Listar equipos de una clase",
        description = "Retorna los equipos requeridos por una clase junto con su disponibilidad.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de equipos de la clase"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<List<ClaseEquipoDTO>> obtenerEquiposDeClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
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
    @Operation(summary = "Obtener detalle de clase con equipos",
        description = "Devuelve información detallada de una clase incluyendo los equipos asociados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de clase"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseResponseDTO> obtenerClaseConEquipos(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
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
    @Operation(summary = "Reservar equipos para una clase",
        description = "Reserva todos los equipos necesarios para iniciar una clase. Si falta disponibilidad, no reserva ninguno.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipos reservados"),
        @ApiResponse(responseCode = "409", description = "No hay equipos suficientes"),
        @ApiResponse(responseCode = "503", description = "Servicio de equipos no disponible")
    })
    public ResponseEntity<ClaseResponseDTO> reservarEquiposParaClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
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
    @Operation(summary = "Liberar equipos de una clase",
        description = "Libera todos los equipos asociados a la clase (al finalizar la clase).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipos liberados"),
        @ApiResponse(responseCode = "503", description = "Servicio de equipos no disponible")
    })
    public ResponseEntity<Map<String, Object>> liberarEquiposDeClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
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
    @Operation(summary = "Verificar disponibilidad de equipos",
        description = "Verifica si todos los equipos requeridos por una clase están disponibles.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad verificada"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
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
    @Operation(summary = "Eliminar equipo de una clase",
        description = "Elimina un equipo requerido previamente asociado a una clase.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipo eliminado de la clase"),
        @ApiResponse(responseCode = "404", description = "Clase o equipo no encontrado")
    })
    public ResponseEntity<Map<String, String>> eliminarEquipoDeClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId,
            @Parameter(description = "ID del equipo", required = true) @PathVariable Long equipoId) {
        
        logger.info("DELETE /gym/clases/{}/equipos/{}", claseId, equipoId);
        
        claseEquipmentService.eliminarEquipoDeClase(claseId, equipoId);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Equipo eliminado de la clase exitosamente");
        
        return ResponseEntity.ok(response);
    }
}
