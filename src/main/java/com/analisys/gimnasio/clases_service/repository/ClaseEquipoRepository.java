package com.analisys.gimnasio.clases_service.repository;

import com.analisys.gimnasio.clases_service.model.ClaseEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaseEquipoRepository extends JpaRepository<ClaseEquipo, Long> {
    
    List<ClaseEquipo> findByClaseId(Long claseId);
    
    @Modifying
    void deleteByClaseIdAndEquipoId(Long claseId, Long equipoId);
    
    boolean existsByClaseIdAndEquipoId(Long claseId, Long equipoId);
}
