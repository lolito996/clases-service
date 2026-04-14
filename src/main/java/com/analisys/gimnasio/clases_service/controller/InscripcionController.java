package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.model.Inscripcion;
import com.analisys.gimnasio.clases_service.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gym/clases/inscripciones")
@Tag(name = "Inscripciones", description = "Gestión de inscripciones de miembros a clases")
@SecurityRequirement(name = "bearer-jwt")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @Operation(summary = "Inscribir miembro en una clase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscripción exitosa"),
            @ApiResponse(responseCode = "400", description = "Miembro ya inscrito o clase llena")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> inscribir(@RequestBody Map<String, Long> body) {
        Long miembroId = body.get("miembroId");
        Long claseId = body.get("claseId");
        try {
            Inscripcion inscripcion = inscripcionService.inscribir(miembroId, claseId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Miembro inscrito exitosamente en la clase");
            response.put("inscripcion", Map.of(
                    "id", inscripcion.getId(),
                    "miembroId", inscripcion.getMiembroId(),
                    "claseId", inscripcion.getClase().getId(),
                    "claseNombre", inscripcion.getClase().getNombre(),
                    "fechaInscripcion", inscripcion.getFechaInscripcion()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener clases de un miembro",
            description = "Retorna las clases en las que está inscrito un miembro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases del miembro")
    })
    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<Clase>> obtenerClasesPorMiembro(
            @Parameter(description = "ID del miembro") @PathVariable Long miembroId) {
        List<Clase> clases = inscripcionService.obtenerClasesPorMiembro(miembroId);
        return ResponseEntity.ok(clases);
    }

    @Operation(summary = "Cancelar inscripción de un miembro en una clase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción cancelada"),
            @ApiResponse(responseCode = "400", description = "Inscripción no encontrada")
    })
    @DeleteMapping("/{miembroId}/{claseId}")
    public ResponseEntity<Map<String, String>> cancelarInscripcion(
            @Parameter(description = "ID del miembro") @PathVariable Long miembroId,
            @Parameter(description = "ID de la clase") @PathVariable Long claseId) {
        try {
            inscripcionService.cancelarInscripcion(miembroId, claseId);
            return ResponseEntity.ok(Map.of("mensaje", "Inscripción cancelada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
