package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.Adoptante;
import com.cibertec.huellaviva.repository.IAdoptanteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdoptanteController {

    @Autowired
    private IAdoptanteRepository adoptanteRepo;

    @GetMapping("/adoptantes")
    public String listarAdoptantes(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("adoptante", new Adoptante());
        model.addAttribute("listaAdoptantes", adoptanteRepo.findAll());
        return "adoptantes";
    }

    @PostMapping("/adoptantes/guardar")
    public String guardarAdoptante(@ModelAttribute Adoptante adoptante, RedirectAttributes flash) {
        try {
            adoptanteRepo.save(adoptante);
            flash.addFlashAttribute("exito", "Adoptante registrado/actualizado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: Verifique que el DNI no esté duplicado.");
        }
        return "redirect:/adoptantes";
    }

    @GetMapping("/adoptantes/editar/{id}")
    public String editarAdoptante(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("adoptante", adoptanteRepo.findById(id).orElse(new Adoptante()));
        model.addAttribute("listaAdoptantes", adoptanteRepo.findAll());
        return "adoptantes";
    }

    @GetMapping("/adoptantes/eliminar/{id}")
    public String eliminarAdoptante(@PathVariable("id") Integer id, RedirectAttributes flash) {
        try {
            adoptanteRepo.deleteById(id);
            flash.addFlashAttribute("exito", "Adoptante eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar porque tiene Actas de Adopción registradas.");
        }
        return "redirect:/adoptantes";
    }
}