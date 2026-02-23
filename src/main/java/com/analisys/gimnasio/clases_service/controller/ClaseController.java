package com.analisys.gimnasio.clases_service.controller;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.service.ClaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/gym/clases")
public class ClaseController {
    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }
    
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

    @GetMapping("/obtener")
    public ResponseEntity<List<Clase>> getAllClases() {
        List<Clase> clases = claseService.obtenerTodasLasClases();
        return ResponseEntity.ok(clases);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarClase(@PathVariable Long id) {
        claseService.eliminarClase(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Clase eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

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