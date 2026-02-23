package com.analisys.gimnasio.clases_service.config;

import com.analisys.gimnasio.clases_service.model.Clase;
import com.analisys.gimnasio.clases_service.repository.ClaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClaseRepository claseRepository;

    public DataLoader(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen datos
        if (claseRepository.count() == 0) {
            cargarDatosIniciales();
            System.out.println("✅ Datos iniciales de clases cargados exitosamente");
        } else {
            System.out.println("ℹ️ La base de datos ya contiene datos");
        }
    }

    private void cargarDatosIniciales() {
        List<Clase> clasesIniciales = Arrays.asList(
            crearClase("Yoga", LocalDateTime.of(2026, 2, 23, 8, 0), 20),
            crearClase("Spinning", LocalDateTime.of(2026, 2, 23, 9, 30), 15),
            crearClase("Zumba", LocalDateTime.of(2026, 2, 23, 11, 0), 25),
            crearClase("CrossFit", LocalDateTime.of(2026, 2, 23, 14, 0), 12),
            crearClase("Pilates", LocalDateTime.of(2026, 2, 23, 16, 0), 18),
            crearClase("Boxeo", LocalDateTime.of(2026, 2, 24, 7, 0), 10),
            crearClase("Funcional", LocalDateTime.of(2026, 2, 24, 10, 0), 20),
            crearClase("Aeróbicos", LocalDateTime.of(2026, 2, 24, 12, 0), 30),
            crearClase("TRX", LocalDateTime.of(2026, 2, 24, 15, 0), 14),
            crearClase("Stretching", LocalDateTime.of(2026, 2, 24, 18, 0), 22)
        );

        claseRepository.saveAll(clasesIniciales);
    }

    private Clase crearClase(String nombre, LocalDateTime horario, int capacidad) {
        Clase clase = new Clase();
        clase.setNombre(nombre);
        clase.setHorario(horario);
        clase.setCapacidadMaxima(capacidad);
        return clase;
    }

    public List<Clase> leerDatosIniciales() {
        return claseRepository.findAll();
    }
}
