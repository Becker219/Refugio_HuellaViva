package com.cibertec.huellaviva.controller;

import com.cibertec.huellaviva.model.Insumo;
import com.cibertec.huellaviva.repository.ICategoriaInsumoRepository;
import com.cibertec.huellaviva.repository.IInsumoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InsumoController {

    @Autowired
    private IInsumoRepository insumoRepo;

    @Autowired
    private ICategoriaInsumoRepository categoriaRepo;

    @GetMapping("/insumos")
    public String listarInsumos(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("insumo", new Insumo());
        model.addAttribute("listaInsumos", insumoRepo.findAll());
        model.addAttribute("listaCategorias", categoriaRepo.findAll());
        return "insumos";
    }

    @PostMapping("/insumos/guardar")
    public String guardarInsumo(@ModelAttribute Insumo insumo, RedirectAttributes flash) {
        try {
            insumoRepo.save(insumo);
            flash.addFlashAttribute("exito", "Insumo médico/alimento guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar el insumo.");
        }
        return "redirect:/insumos";
    }

    @GetMapping("/insumos/editar/{id}")
    public String editarInsumo(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("insumo", insumoRepo.findById(id).orElse(new Insumo()));
        model.addAttribute("listaInsumos", insumoRepo.findAll());
        model.addAttribute("listaCategorias", categoriaRepo.findAll());
        return "insumos";
    }

    @GetMapping("/insumos/eliminar/{id}")
    public String eliminarInsumo(@PathVariable("id") Integer id, RedirectAttributes flash) {
        try {
            insumoRepo.deleteById(id);
            flash.addFlashAttribute("exito", "Insumo eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar porque forma parte de kits de adopción entregados.");
        }
        return "redirect:/insumos";
    }

    // CASO DE USO DE CONSULTA 2: AUDITORÍA DE STOCK E INSUMOS
    @GetMapping("/consultas/insumos")
    public String consultarInsumos(
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean soloCritico,
            Model model,
            HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        model.addAttribute("listaCategorias", categoriaRepo.findAll());
        model.addAttribute("resultados", insumoRepo.buscarAuditorInsumos(idCategoria, texto, soloCritico));

        model.addAttribute("idCategoriaSel", idCategoria);
        model.addAttribute("textoSel", texto);
        model.addAttribute("criticoSel", soloCritico);
        return "consulta-insumos";
    }
}