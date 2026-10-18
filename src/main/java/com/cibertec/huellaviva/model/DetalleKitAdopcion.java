package com.cibertec.huellaviva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_detalle_kit_adopcion")
public class DetalleKitAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    private Integer cantidad;

    @Column(name = "costo_referencial")
    private BigDecimal costoReferencial;

    @ManyToOne
    @JoinColumn(name = "id_adopcion")
    private ActaAdopcion actaAdopcion;

    @ManyToOne
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoReferencial() {
        return costoReferencial;
    }

    public void setCostoReferencial(BigDecimal costoReferencial) {
        this.costoReferencial = costoReferencial;
    }

    public ActaAdopcion getActaAdopcion() {
        return actaAdopcion;
    }

    public void setActaAdopcion(ActaAdopcion actaAdopcion) {
        this.actaAdopcion = actaAdopcion;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }
}