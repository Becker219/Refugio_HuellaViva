package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.Rol;
import com.cibertec.huellaviva.model.Usuario;
import com.cibertec.huellaviva.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @Autowired
    private IMascotaRepository mascotaRepo;

    @Autowired
    private IInsumoRepository insumoRepo;

    @Autowired
    private IAdoptanteRepository adoptanteRepo;

    @Autowired
    private IActaAdopcionRepository actaRepo;

    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        Usuario user = usuarioRepo.autenticar(username, password);
        if (user != null) {
            session.setAttribute("usuarioLogueado", user);
            session.setAttribute("rolUsuario", user.getRol().getNombre());
            return "redirect:/menu";
        }
        model.addAttribute("error", "Usuario/Correo o contraseña incorrectos");
        return "login";
    }

    // VISTA DE REGISTRO PARA NUEVOS ADOPTANTES
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    // PROCESAR REGISTRO (Nombre, Correo y Contraseña)
    @PostMapping("/registro")
    public String registrarAdoptante(@RequestParam String nombreCompleto,
                                     @RequestParam String correo,
                                     @RequestParam String password,
                                     RedirectAttributes flash,
                                     Model model) {
        if (usuarioRepo.existsByCorreo(correo)) {
            model.addAttribute("error", "El correo electrónico ya se encuentra registrado.");
            return "registro";
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombreCompleto(nombreCompleto);
        nuevo.setCorreo(correo);
        nuevo.setUsername(correo); // Usa su correo como credencial de acceso
        nuevo.setPassword(password);
        nuevo.setActivo(true);

        Rol rolAdoptante = new Rol();
        rolAdoptante.setIdRol(3); // ID 3 = ADOPTANTE
        nuevo.setRol(rolAdoptante);

        usuarioRepo.save(nuevo);
        flash.addFlashAttribute("exito", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión con tu correo.");
        return "redirect:/login";
    }

    @GetMapping("/menu")
    public String mostrarMenu(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        model.addAttribute("totalMascotas", mascotaRepo.count());
        model.addAttribute("mascotasAptas", mascotaRepo.findByEstado("APTO_ADOPCION").size());
        model.addAttribute("totalInsumos", insumoRepo.count());
        model.addAttribute("totalAdoptantes", adoptanteRepo.count());
        model.addAttribute("totalAdopciones", actaRepo.count());
        return "menu";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}