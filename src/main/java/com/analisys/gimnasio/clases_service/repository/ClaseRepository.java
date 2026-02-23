package com.analisys.gimnasio.clases_service.repository;

import com.analisys.gimnasio.clases_service.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    
}