package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.CategoriaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaInsumoRepository extends JpaRepository<CategoriaInsumo, Integer> {
}