package com.cibertec.huellaviva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_acta_adopcion")
public class ActaAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adopcion")
    private Integer idAdopcion;

    @Column(name = "num_acta", nullable = false, unique = true)
    private String numActa;

    @Column(name = "fecha_adopcion")
    private LocalDateTime fechaAdopcion = LocalDateTime.now();

    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "id_adoptante", nullable = false)
    private Adoptante adoptante;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "actaAdopcion", cascade = CascadeType.ALL)
    private List<DetalleKitAdopcion> detallesKit = new ArrayList<>();

    @OneToMany(mappedBy = "actaAdopcion", cascade = CascadeType.ALL)
    private List<ControlPostAdopcion> controles = new ArrayList<>();

    public Integer getIdAdopcion() {
        return idAdopcion;
    }

    public void setIdAdopcion(Integer idAdopcion) {
        this.idAdopcion = idAdopcion;
    }

    public String getNumActa() {
        return numActa;
    }

    public void setNumActa(String numActa) {
        this.numActa = numActa;
    }

    public LocalDateTime getFechaAdopcion() {
        return fechaAdopcion;
    }

    public void setFechaAdopcion(LocalDateTime fechaAdopcion) {
        this.fechaAdopcion = fechaAdopcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Adoptante getAdoptante() {
        return adoptante;
    }

    public void setAdoptante(Adoptante adoptante) {
        this.adoptante = adoptante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleKitAdopcion> getDetallesKit() {
        return detallesKit;
    }

    public void setDetallesKit(List<DetalleKitAdopcion> detallesKit) {
        this.detallesKit = detallesKit;
    }

    public List<ControlPostAdopcion> getControles() {
        return controles;
    }

    public void setControles(List<ControlPostAdopcion> controles) {
        this.controles = controles;
    }
}