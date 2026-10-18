package com.cibertec.huellaviva.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_mascota")
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Integer idMascota;

    @Column(name = "codigo_interno", nullable = false, unique = true)
    private String codigoInterno;

    private String nombre;

    @Column(name = "edad_meses")
    private Integer edadMeses;

    private BigDecimal peso;
    private String tamanio;

    @Column(name = "nivel_energia")
    private String nivelEnergia;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_rescate")
    private LocalDate fechaRescate;

    @Column(name = "lugar_rescate")
    private String lugarRescate;

    private String estado = "APTO_ADOPCION";
    private String historia;

    @ManyToOne
    @JoinColumn(name = "id_especie")
    private Especie especie;

    public Integer getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Integer idMascota) {
        this.idMascota = idMascota;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdadMeses() {
        return edadMeses;
    }

    public void setEdadMeses(Integer edadMeses) {
        this.edadMeses = edadMeses;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public String getNivelEnergia() {
        return nivelEnergia;
    }

    public void setNivelEnergia(String nivelEnergia) {
        this.nivelEnergia = nivelEnergia;
    }

    public LocalDate getFechaRescate() {
        return fechaRescate;
    }

    public void setFechaRescate(LocalDate fechaRescate) {
        this.fechaRescate = fechaRescate;
    }

    public String getLugarRescate() {
        return lugarRescate;
    }

    public void setLugarRescate(String lugarRescate) {
        this.lugarRescate = lugarRescate;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
}