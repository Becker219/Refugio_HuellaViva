package com.cibertec.huellaviva.service;

import com.cibertec.huellaviva.model.*;
import com.cibertec.huellaviva.repository.IActaAdopcionRepository;
import com.cibertec.huellaviva.repository.IInsumoRepository;
import com.cibertec.huellaviva.repository.IMascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdopcionService {

    @Autowired
    private IActaAdopcionRepository actaRepo;

    @Autowired
    private IMascotaRepository mascotaRepo;

    @Autowired
    private IInsumoRepository insumoRepo;

    @Transactional(rollbackFor = Exception.class)
    public ActaAdopcion registrarAdopcionTransaccional(ActaAdopcion acta, List<DetalleKitAdopcion> carritoKit) throws Exception {

       
    	// 1. Obtener la mascota y asegurar que quede en estado ADOPTADO
        Mascota mascota = mascotaRepo.findById(acta.getMascota().getIdMascota())
                .orElseThrow(() -> new Exception("La mascota seleccionada no existe."));

        mascota.setEstado("ADOPTADO");
        mascotaRepo.save(mascota);
        // 2. Descontar stock de cada insumo del kit sanitario y asociarlo al acta
        for (DetalleKitAdopcion item : carritoKit) {
            Insumo insumo = insumoRepo.findById(item.getInsumo().getIdInsumo())
                    .orElseThrow(() -> new Exception("Insumo no encontrado."));

            if (insumo.getStockActual() < item.getCantidad()) {
                throw new Exception("Stock insuficiente para: " + insumo.getDescripcion() 
                        + " (Disponible: " + insumo.getStockActual() + ")");
            }

            // Descontar stock
            insumo.setStockActual(insumo.getStockActual() - item.getCantidad());
            insumoRepo.save(insumo);

            // Vincular detalle con la cabecera
            DetalleKitAdopcion nuevoDetalle = new DetalleKitAdopcion();
            nuevoDetalle.setInsumo(insumo);
            nuevoDetalle.setCantidad(item.getCantidad());
            nuevoDetalle.setCostoReferencial(insumo.getCostoUnitario());
            nuevoDetalle.setActaAdopcion(acta);
            acta.getDetallesKit().add(nuevoDetalle);
        }

        // 3. Generar automáticamente los 3 controles post-adopción (30, 60 y 90 días)
        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= 3; i++) {
            ControlPostAdopcion control = new ControlPostAdopcion();
            control.setActaAdopcion(acta);
            control.setNumeroVisita(i);
            control.setFechaProgramada(hoy.plusDays(i * 30L));
            control.setEstadoControl("PENDIENTE");
            control.setObservacionVeterinaria("Control obligatorio N° " + i + " (" + (i * 30) + " días post-adopción)");
            acta.getControles().add(control);
        }

        // 4. Guardar Acta + Detalles + Controles en cascada
        acta.setFechaAdopcion(LocalDateTime.now());
        return actaRepo.save(acta);
    }
}