package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.dto.AsignarEntrenadorRequest;
import com.analisys.gimnasio.clases_service.dto.ClaseConEntrenadorDTO;
import com.analisys.gimnasio.clases_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.service.ClaseTrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para la integración Clases - Entrenadores
 */
@RestController
@RequestMapping("/gym/clases")
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
    public ResponseEntity<ClaseConEntrenadorDTO> asignarEntrenador(
            @PathVariable Long claseId,
            @RequestBody AsignarEntrenadorRequest request) {
        ClaseConEntrenadorDTO resultado = claseTrainerService.asignarEntrenador(claseId, request.getEntrenadorId());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el detalle de una clase con info del entrenador
     * GET /gym/clases/{id}/con-entrenador
     */
    @GetMapping("/{claseId}/con-entrenador")
    public ResponseEntity<ClaseConEntrenadorDTO> obtenerClaseConEntrenador(@PathVariable Long claseId) {
        ClaseConEntrenadorDTO resultado = claseTrainerService.obtenerClaseConEntrenador(claseId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista todas las clases con información de entrenadores
     * GET /gym/clases/con-entrenadores
     */
    @GetMapping("/con-entrenadores")
    public ResponseEntity<List<ClaseConEntrenadorDTO>> listarClasesConEntrenadores() {
        List<ClaseConEntrenadorDTO> clases = claseTrainerService.listarClasesConEntrenadores();
        return ResponseEntity.ok(clases);
    }

    /**
     * Busca clases por especialidad del entrenador
     * GET /gym/clases/por-especialidad?especialidad=Yoga
     */
    @GetMapping("/por-especialidad")
    public ResponseEntity<List<ClaseConEntrenadorDTO>> buscarClasesPorEspecialidad(
            @RequestParam String especialidad) {
        List<ClaseConEntrenadorDTO> clases = claseTrainerService.buscarClasesPorEspecialidad(especialidad);
        return ResponseEntity.ok(clases);
    }

    /**
     * Remueve el entrenador de una clase
     * DELETE /gym/clases/{id}/entrenador
     */
    @DeleteMapping("/{claseId}/entrenador")
    public ResponseEntity<Clase> removerEntrenador(@PathVariable Long claseId) {
        Clase clase = claseTrainerService.removerEntrenador(claseId);
        return ResponseEntity.ok(clase);
    }

    /**
     * Lista todos los entrenadores disponibles (desde servicio de entrenadores)
     * GET /gym/clases/entrenadores-disponibles
     */
    @GetMapping("/entrenadores-disponibles")
    public ResponseEntity<List<TrainerResponseDTO>> listarEntrenadoresDisponibles() {
        List<TrainerResponseDTO> entrenadores = claseTrainerService.listarEntrenadoresDisponibles();
        return ResponseEntity.ok(entrenadores);
    }
}
