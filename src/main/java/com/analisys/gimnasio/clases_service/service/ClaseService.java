package com.analisys.gimnasio.clases_service.service;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaseService {
    
    private final ClaseRepository claseRepository;

    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
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
}
