package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.Mascota;
import com.cibertec.huellaviva.repository.IEspecieRepository;
import com.cibertec.huellaviva.repository.IMascotaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MascotaController {

    @Autowired
    private IMascotaRepository mascotaRepo;

    @Autowired
    private IEspecieRepository especieRepo;

    // 1. LISTAR Y MOSTRAR FORMULARIO DE MANTENIMIENTO
    @GetMapping("/mascotas")
    public String listarMascotas(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("mascota", new Mascota());
        model.addAttribute("listaMascotas", mascotaRepo.findAll());
        model.addAttribute("listaEspecies", especieRepo.findAll());
        return "mascotas";
    }

    // 2. GUARDAR O ACTUALIZAR MASCOTA
    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, RedirectAttributes flash) {
        try {
            mascotaRepo.save(mascota);
            flash.addFlashAttribute("exito", "Animal rescatado guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: Verifique que el código interno no esté repetido.");
        }
        return "redirect:/mascotas";
    }

    // 3. CARGAR DATOS PARA EDITAR
    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Mascota mascota = mascotaRepo.findById(id).orElse(new Mascota());
        model.addAttribute("mascota", mascota);
        model.addAttribute("listaMascotas", mascotaRepo.findAll());
        model.addAttribute("listaEspecies", especieRepo.findAll());
        return "mascotas";
    }

    // 4. ELIMINAR REGISTRO
    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable("id") Integer id, RedirectAttributes flash) {
        try {
            mascotaRepo.deleteById(id);
            flash.addFlashAttribute("exito", "Registro eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar porque ya tiene un Acta de Adopción vinculada.");
        }
        return "redirect:/mascotas";
    }

    // 5. CASO DE USO DE CONSULTA 1: BUSCADOR MULTICRITERIO
    @GetMapping("/consultas/mascotas")
    public String consultarMascotas(
            @RequestParam(required = false) Integer idEspecie,
            @RequestParam(required = false) String tamanio,
            @RequestParam(required = false) String nivelEnergia,
            @RequestParam(required = false) String estado,
            Model model,
            HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("listaEspecies", especieRepo.findAll());
        model.addAttribute("resultados", mascotaRepo.buscarMulticriterio(idEspecie, tamanio, nivelEnergia, estado));
        
        // Mantener valores seleccionados en los filtros
        model.addAttribute("idEspecieSel", idEspecie);
        model.addAttribute("tamanioSel", tamanio);
        model.addAttribute("energiaSel", nivelEnergia);
        model.addAttribute("estadoSel", estado);
        return "consulta-mascotas";
    }
}