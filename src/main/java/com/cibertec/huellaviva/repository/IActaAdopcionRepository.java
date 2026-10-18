package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.ActaAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActaAdopcionRepository extends JpaRepository<ActaAdopcion, Integer> {
}