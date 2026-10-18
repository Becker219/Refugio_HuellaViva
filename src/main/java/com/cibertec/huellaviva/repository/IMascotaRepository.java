package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IMascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findByEstado(String estado);
    
 // Muestra mascotas APTAS o aquellas que ya fueron ADOPTADAS por solicitud pero aún no tienen Acta ni Kit
    @Query("SELECT m FROM Mascota m WHERE m.estado = 'APTO_ADOPCION' OR " +
           "(m.estado = 'ADOPTADO' AND m.idMascota NOT IN (SELECT a.mascota.idMascota FROM ActaAdopcion a))")
    List<Mascota> buscarDisponiblesParaActa();

    @Query("SELECT m FROM Mascota m WHERE " +
           "(:idEspecie IS NULL OR m.especie.idEspecie = :idEspecie) AND " +
           "(:tamanio IS NULL OR :tamanio = '' OR m.tamanio = :tamanio) AND " +
           "(:nivelEnergia IS NULL OR :nivelEnergia = '' OR m.nivelEnergia = :nivelEnergia) AND " +
           "(:estado IS NULL OR :estado = '' OR m.estado = :estado)")
    List<Mascota> buscarMulticriterio(
            @Param("idEspecie") Integer idEspecie,
            @Param("tamanio") String tamanio,
            @Param("nivelEnergia") String nivelEnergia,
            @Param("estado") String estado);
}