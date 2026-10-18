package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEspecieRepository extends JpaRepository<Especie, Integer> {
}