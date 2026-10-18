package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.*;
import com.cibertec.huellaviva.repository.*;
import com.cibertec.huellaviva.service.AdopcionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/adopciones")
public class AdopcionController {

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private IMascotaRepository mascotaRepo;

    @Autowired
    private IAdoptanteRepository adoptanteRepo;

    @Autowired
    private IInsumoRepository insumoRepo;

    @Autowired
    private IActaAdopcionRepository actaRepo;

    @SuppressWarnings("unchecked")
    @GetMapping("/nueva")
    public String mostrarFormularioTransaccion(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        List<DetalleKitAdopcion> carritoKit = (List<DetalleKitAdopcion>) session.getAttribute("carritoKit");
        if (carritoKit == null) {
            carritoKit = new ArrayList<>();
            session.setAttribute("carritoKit", carritoKit);
        }

        // Generar código de acta sugerido automáticamente
        long totalActas = actaRepo.count() + 1;
        String numActaSugerido = String.format("ACTA-HV-%04d", totalActas);

        model.addAttribute("numActaSugerido", numActaSugerido);
        model.addAttribute("mascotasAptas", mascotaRepo.buscarDisponiblesParaActa());
        model.addAttribute("adoptantesAptos", adoptanteRepo.findByEstadoEvaluacion("APTO"));
        model.addAttribute("insumosDisponibles", insumoRepo.findByStockActualGreaterThan(0));
        model.addAttribute("carritoKit", carritoKit);
        model.addAttribute("historialActas", actaRepo.findAll());

        return "nueva-adopcion";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/agregar-insumo")
    public String agregarInsumoAlKit(
            @RequestParam Integer idInsumo,
            @RequestParam Integer cantidad,
            HttpSession session,
            RedirectAttributes flash) {

        List<DetalleKitAdopcion> carritoKit = (List<DetalleKitAdopcion>) session.getAttribute("carritoKit");
        if (carritoKit == null) {
            carritoKit = new ArrayList<>();
        }

        Insumo insumo = insumoRepo.findById(idInsumo).orElse(null);
        if (insumo != null) {
            if (cantidad > insumo.getStockActual()) {
                flash.addFlashAttribute("error", "La cantidad supera el stock disponible (" + insumo.getStockActual() + ").");
            } else {
                DetalleKitAdopcion item = new DetalleKitAdopcion();
                item.setInsumo(insumo);
                item.setCantidad(cantidad);
                item.setCostoReferencial(insumo.getCostoUnitario());
                carritoKit.add(item);
                session.setAttribute("carritoKit", carritoKit);
                flash.addFlashAttribute("exito", "Insumo agregado al kit sanitario de salida.");
            }
        }
        return "redirect:/adopciones/nueva";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/quitar-insumo/{index}")
    public String quitarInsumoDelKit(@PathVariable int index, HttpSession session) {
        List<DetalleKitAdopcion> carritoKit = (List<DetalleKitAdopcion>) session.getAttribute("carritoKit");
        if (carritoKit != null && index >= 0 && index < carritoKit.size()) {
            carritoKit.remove(index);
            session.setAttribute("carritoKit", carritoKit);
        }
        return "redirect:/adopciones/nueva";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/registrar")
    public String procesarTransaccionAdopcion(
            @RequestParam String numActa,
            @RequestParam Integer idMascota,
            @RequestParam Integer idAdoptante,
            @RequestParam(required = false) String observaciones,
            HttpSession session,
            RedirectAttributes flash) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        List<DetalleKitAdopcion> carritoKit = (List<DetalleKitAdopcion>) session.getAttribute("carritoKit");
        if (carritoKit == null || carritoKit.isEmpty()) {
            flash.addFlashAttribute("error", "Debe agregar al menos un insumo al Kit Sanitario antes de formalizar la adopción.");
            return "redirect:/adopciones/nueva";
        }

        try {
            ActaAdopcion acta = new ActaAdopcion();
            acta.setNumActa(numActa);
            acta.setObservaciones(observaciones);

            Mascota m = new Mascota();
            m.setIdMascota(idMascota);
            acta.setMascota(m);

            Adoptante a = new Adoptante();
            a.setIdAdoptante(idAdoptante);
            acta.setAdoptante(a);

            acta.setUsuario(usuarioLogueado);

            adopcionService.registrarAdopcionTransaccional(acta, carritoKit);

            // Limpiar carrito tras éxito de la transacción
            session.removeAttribute("carritoKit");
            flash.addFlashAttribute("exito", "¡Transacción exitosa! Se registró el " + numActa + ", se descontó el stock y se programaron las 3 visitas post-adopción.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Rollback de Transacción: " + e.getMessage());
        }

        return "redirect:/adopciones/nueva";
    }
}