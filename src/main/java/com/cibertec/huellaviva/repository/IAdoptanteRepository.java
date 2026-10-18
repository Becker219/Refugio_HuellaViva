package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.Adoptante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAdoptanteRepository extends JpaRepository<Adoptante, Integer> {
    List<Adoptante> findByEstadoEvaluacion(String estadoEvaluacion);
    Optional<Adoptante> findByDni(String dni);
}