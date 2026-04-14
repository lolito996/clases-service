package com.analisys.gimnasio.clases_service.repository;

import com.analisys.gimnasio.clases_service.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByMiembroId(Long miembroId);
    boolean existsByMiembroIdAndClaseId(Long miembroId, Long claseId);
    void deleteByMiembroIdAndClaseId(Long miembroId, Long claseId);
}
