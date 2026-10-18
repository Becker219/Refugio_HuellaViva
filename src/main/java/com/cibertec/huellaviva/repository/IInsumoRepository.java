package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IInsumoRepository extends JpaRepository<Insumo, Integer> {

    List<Insumo> findByStockActualGreaterThan(Integer stock);

    @Query("SELECT i FROM Insumo i WHERE " +
           "(:idCategoria IS NULL OR i.categoria.idCategoria = :idCategoria) AND " +
           "(:texto IS NULL OR :texto = '' OR LOWER(i.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))) AND " +
           "(:soloCritico IS NULL OR :soloCritico = false OR i.stockActual <= i.stockMinimo)")
    List<Insumo> buscarAuditorInsumos(
            @Param("idCategoria") Integer idCategoria,
            @Param("texto") String texto,
            @Param("soloCritico") Boolean soloCritico);
}