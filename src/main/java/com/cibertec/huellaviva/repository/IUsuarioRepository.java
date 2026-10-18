package com.cibertec.huellaviva.repository;

import com.cibertec.huellaviva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT u FROM Usuario u WHERE (u.username = :credencial OR u.correo = :credencial) AND u.password = :password AND u.activo = true")
    Usuario autenticar(@Param("credencial") String credencial, @Param("password") String password);

    boolean existsByCorreo(String correo);
}