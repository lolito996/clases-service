package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.dto.AsignarEntrenadorRequest;
import com.analisys.gimnasio.clases_service.dto.ClaseConEntrenadorDTO;
import com.analisys.gimnasio.clases_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.service.ClaseTrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para la integración Clases - Entrenadores
 */
@RestController
@RequestMapping("/gym/clases")
@Tag(name = "Clases - Entrenadores", description = "Integración de clases con entrenadores (asignación, consulta y listado)")
@SecurityRequirement(name = "bearer-jwt")
public class ClaseTrainerController {

    private final ClaseTrainerService claseTrainerService;

    public ClaseTrainerController(ClaseTrainerService claseTrainerService) {
        this.claseTrainerService = claseTrainerService;
    }

    /**
     * Asigna un entrenador a una clase
     * POST /gym/clases/{id}/entrenador
     */
    @PostMapping("/{claseId}/entrenador")
    @Operation(summary = "Asignar entrenador a una clase",
        description = "Asigna un entrenador a una clase existente usando el ID del entrenador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrenador asignado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Clase o entrenador no encontrado"),
        @ApiResponse(responseCode = "503", description = "Servicio de entrenadores no disponible")
    })
    public ResponseEntity<ClaseConEntrenadorDTO> asignarEntrenador(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId,
            @RequestBody AsignarEntrenadorRequest request) {
        ClaseConEntrenadorDTO resultado = claseTrainerService.asignarEntrenador(claseId, request.getEntrenadorId());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el detalle de una clase con info del entrenador
     * GET /gym/clases/{id}/con-entrenador
     */
    @GetMapping("/{claseId}/con-entrenador")
    @Operation(summary = "Obtener clase con entrenador",
        description = "Devuelve el detalle de una clase incluyendo información del entrenador asignado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de clase con entrenador"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
        @ApiResponse(responseCode = "503", description = "Servicio de entrenadores no disponible")
    })
    public ResponseEntity<ClaseConEntrenadorDTO> obtenerClaseConEntrenador(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
        ClaseConEntrenadorDTO resultado = claseTrainerService.obtenerClaseConEntrenador(claseId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista todas las clases con información de entrenadores
     * GET /gym/clases/con-entrenadores
     */
    @GetMapping("/con-entrenadores")
    @Operation(summary = "Listar clases con entrenadores",
        description = "Retorna todas las clases con información de sus entrenadores (si aplica).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de clases con entrenadores"),
        @ApiResponse(responseCode = "503", description = "Servicio de entrenadores no disponible")
    })
    public ResponseEntity<List<ClaseConEntrenadorDTO>> listarClasesConEntrenadores() {
        List<ClaseConEntrenadorDTO> clases = claseTrainerService.listarClasesConEntrenadores();
        return ResponseEntity.ok(clases);
    }

    /**
     * Busca clases por especialidad del entrenador
     * GET /gym/clases/por-especialidad?especialidad=Yoga
     */
    @GetMapping("/por-especialidad")
    @Operation(summary = "Buscar clases por especialidad",
        description = "Retorna clases filtradas por la especialidad del entrenador (parámetro query).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado filtrado"),
        @ApiResponse(responseCode = "503", description = "Servicio de entrenadores no disponible")
    })
    public ResponseEntity<List<ClaseConEntrenadorDTO>> buscarClasesPorEspecialidad(
            @Parameter(description = "Especialidad del entrenador", required = true) @RequestParam String especialidad) {
        List<ClaseConEntrenadorDTO> clases = claseTrainerService.buscarClasesPorEspecialidad(especialidad);
        return ResponseEntity.ok(clases);
    }

    /**
     * Remueve el entrenador de una clase
     * DELETE /gym/clases/{id}/entrenador
     */
    @DeleteMapping("/{claseId}/entrenador")
    @Operation(summary = "Remover entrenador de una clase",
        description = "Elimina la asignación de entrenador para una clase existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrenador removido"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<Clase> removerEntrenador(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long claseId) {
        Clase clase = claseTrainerService.removerEntrenador(claseId);
        return ResponseEntity.ok(clase);
    }

    /**
     * Lista todos los entrenadores disponibles (desde servicio de entrenadores)
     * GET /gym/clases/entrenadores-disponibles
     */
    @GetMapping("/entrenadores-disponibles")
    @Operation(summary = "Listar entrenadores disponibles",
        description = "Obtiene la lista de entrenadores disponibles desde el microservicio de entrenadores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de entrenadores"),
        @ApiResponse(responseCode = "503", description = "Servicio de entrenadores no disponible")
    })
    public ResponseEntity<List<TrainerResponseDTO>> listarEntrenadoresDisponibles() {
        List<TrainerResponseDTO> entrenadores = claseTrainerService.listarEntrenadoresDisponibles();
        return ResponseEntity.ok(entrenadores);
    }
}
