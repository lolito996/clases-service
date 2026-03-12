package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.service.ClaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/gym/clases")
@Tag(name = "Clases", description = "Endpoints para programar y gestionar clases")
@SecurityRequirement(name = "bearer-jwt")
public class ClaseController {
    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }
    
    @Operation(
        summary = "Programar una clase",
        description = "Crea y agenda una nueva clase. Retorna la clase creada y un mensaje de confirmación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Clase creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/programar")
    public ResponseEntity<Map<String, Object>> crearClase(@RequestBody Clase clase) {
        try {
            Clase nuevaClase = claseService.crearClase(clase);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Clase asignada exitosamente");
            response.put("clase", nuevaClase);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(
        summary = "Obtener todas las clases",
        description = "Retorna un listado con todas las clases registradas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de clases")
    })
    @GetMapping("/obtener")
    public ResponseEntity<List<Clase>> getAllClases() {
        List<Clase> clases = claseService.obtenerTodasLasClases();
        return ResponseEntity.ok(clases);
    }

    @Operation(
        summary = "Eliminar una clase",
        description = "Elimina una clase por ID y retorna un mensaje de confirmación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clase eliminada"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarClase(
            @Parameter(description = "ID de la clase", required = true) @PathVariable Long id) {
        claseService.eliminarClase(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Clase eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Leer datos iniciales",
        description = "Carga/lee los datos iniciales de clases y retorna el detalle junto con un conteo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos iniciales obtenidos")
    })
    @GetMapping("/datos-iniciales")
    public ResponseEntity<Map<String, Object>> leerDatosIniciales() {
        List<Clase> clases = claseService.leerDatosIniciales();
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Datos iniciales obtenidos exitosamente");
        response.put("cantidad", clases.size());
        response.put("clases", clases);
        return ResponseEntity.ok(response);
    }
}