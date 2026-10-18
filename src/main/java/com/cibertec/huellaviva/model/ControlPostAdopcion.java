package com.cibertec.huellaviva.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_control_post_adopcion")
public class ControlPostAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control")
    private Integer idControl;

    @Column(name = "numero_visita")
    private Integer numeroVisita;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "estado_control")
    private String estadoControl = "PENDIENTE";

    @Column(name = "observacion_veterinaria")
    private String observacionVeterinaria;

    @ManyToOne
    @JoinColumn(name = "id_adopcion")
    private ActaAdopcion actaAdopcion;

    public Integer getIdControl() {
        return idControl;
    }

    public void setIdControl(Integer idControl) {
        this.idControl = idControl;
    }

    public Integer getNumeroVisita() {
        return numeroVisita;
    }

    public void setNumeroVisita(Integer numeroVisita) {
        this.numeroVisita = numeroVisita;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getEstadoControl() {
        return estadoControl;
    }

    public void setEstadoControl(String estadoControl) {
        this.estadoControl = estadoControl;
    }

    public String getObservacionVeterinaria() {
        return observacionVeterinaria;
    }

    public void setObservacionVeterinaria(String observacionVeterinaria) {
        this.observacionVeterinaria = observacionVeterinaria;
    }

    public ActaAdopcion getActaAdopcion() {
        return actaAdopcion;
    }

    public void setActaAdopcion(ActaAdopcion actaAdopcion) {
        this.actaAdopcion = actaAdopcion;
    }
}