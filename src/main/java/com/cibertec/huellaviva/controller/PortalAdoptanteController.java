package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.Adoptante;
import com.cibertec.huellaviva.model.Mascota;
import com.cibertec.huellaviva.model.SolicitudAdopcion;
import com.cibertec.huellaviva.model.Usuario;
import com.cibertec.huellaviva.repository.IAdoptanteRepository;
import com.cibertec.huellaviva.repository.IMascotaRepository;
import com.cibertec.huellaviva.repository.ISolicitudAdopcionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/portal")
public class PortalAdoptanteController {

    @Autowired
    private IMascotaRepository mascotaRepo;

    @Autowired
    private ISolicitudAdopcionRepository solicitudRepo;

    @Autowired
    private IAdoptanteRepository adoptanteRepo;

    // 1. VER CATÁLOGO DE MASCOTAS APTAS PARA ADOPCIÓN
    @GetMapping("/mascotas")
    public String verMascotasAptas(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("mascotasAptas", mascotaRepo.findByEstado("APTO_ADOPCION"));
        return "portal-mascotas";
    }

    // 2. MOSTRAR FORMULARIO DE SOLICITUD DE ADOPCIÓN
    @GetMapping("/solicitar/{idMascota}")
    public String mostrarFormularioSolicitud(@PathVariable Integer idMascota, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Mascota mascota = mascotaRepo.findById(idMascota).orElse(null);
        if (mascota == null || !"APTO_ADOPCION".equals(mascota.getEstado())) {
            return "redirect:/portal/mascotas";
        }

        model.addAttribute("mascota", mascota);
        model.addAttribute("solicitud", new SolicitudAdopcion());
        return "solicitar-adopcion";
    }

    // 3. GUARDAR LA SOLICITUD DE ADOPCIÓN Y REGISTRAR EN PADRÓN DE ADOPTANTES
    @PostMapping("/solicitar")
    public String enviarSolicitud(@ModelAttribute SolicitudAdopcion solicitud,
                                  @RequestParam Integer idMascota,
                                  HttpSession session,
                                  RedirectAttributes flash) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        Mascota mascota = mascotaRepo.findById(idMascota).orElse(null);
        if (mascota == null) return "redirect:/portal/mascotas";

        solicitud.setMascota(mascota);
        solicitud.setUsuario(user);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstadoSolicitud("PENDIENTE");
        solicitudRepo.save(solicitud);

        // Sincronizar automáticamente con tb_adoptante para que el Admin lo tenga en el padrón
        Adoptante adoptante = adoptanteRepo.findByDni(solicitud.getDni()).orElse(new Adoptante());
        adoptante.setDni(solicitud.getDni());
        adoptante.setNombres(user.getNombreCompleto());
        if (adoptante.getApellidos() == null) {
            adoptante.setApellidos("(Registrado Online)");
        }
        adoptante.setTelefono(solicitud.getTelefono());
        adoptante.setCorreo(user.getCorreo());
        adoptante.setDireccion(solicitud.getDireccion());
        adoptante.setTipoVivienda(solicitud.getTipoVivienda());
        if (adoptante.getIdAdoptante() == null) {
            adoptante.setEstadoEvaluacion("APTO");
        }
        adoptanteRepo.save(adoptante);

        flash.addFlashAttribute("exito", "¡Tu solicitud para adoptar a " + mascota.getNombre() + " ha sido enviada con éxito!");
        return "redirect:/portal/mis-solicitudes";
    }

    // 4. VER SOLICITUDES (El Adoptante ve las suyas; el Admin ve todas y puede aprobarlas)
    @GetMapping("/mis-solicitudes")
    public String verSolicitudes(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        String rol = (String) session.getAttribute("rolUsuario");
        if ("ADMINISTRADOR".equals(rol) || "VETERINARIO".equals(rol)) {
            model.addAttribute("listaSolicitudes", solicitudRepo.findAllByOrderByFechaSolicitudDesc());
        } else {
            model.addAttribute("listaSolicitudes", solicitudRepo.findByUsuarioIdUsuarioOrderByFechaSolicitudDesc(user.getIdUsuario()));
        }
        return "mis-solicitudes";
    }

    // 5. APROBAR / RECHAZAR SOLICITUD (Para el Administrador)
 // 5. APROBAR / RECHAZAR SOLICITUD Y ACTUALIZAR ESTADO DE LA MASCOTA
    @GetMapping("/solicitudes/estado/{id}/{nuevoEstado}")
    public String cambiarEstadoSolicitud(@PathVariable Integer id,
                                         @PathVariable String nuevoEstado,
                                         RedirectAttributes flash) {
        SolicitudAdopcion sol = solicitudRepo.findById(id).orElse(null);
        if (sol != null) {
            sol.setEstadoSolicitud(nuevoEstado);
            solicitudRepo.save(sol);

            // Actualizar automáticamente el estado de la mascota en tb_mascota
            Mascota mascota = sol.getMascota();
            if ("APROBADA".equals(nuevoEstado)) {
                mascota.setEstado("ADOPTADO");
                mascotaRepo.save(mascota);
                flash.addFlashAttribute("exito", "Solicitud APROBADA. La mascota " + mascota.getNombre() + " pasó al estado ADOPTADO.");
            } else if ("RECHAZADA".equals(nuevoEstado)) {
                mascota.setEstado("APTO_ADOPCION");
                mascotaRepo.save(mascota);
                flash.addFlashAttribute("exito", "Solicitud RECHAZADA. La mascota " + mascota.getNombre() + " sigue disponible.");
            }
        }
        return "redirect:/portal/mis-solicitudes";
    }
}