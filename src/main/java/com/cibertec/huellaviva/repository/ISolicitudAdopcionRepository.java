package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.SolicitudAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ISolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {
    List<SolicitudAdopcion> findByUsuarioIdUsuarioOrderByFechaSolicitudDesc(Integer idUsuario);
    List<SolicitudAdopcion> findAllByOrderByFechaSolicitudDesc();
}