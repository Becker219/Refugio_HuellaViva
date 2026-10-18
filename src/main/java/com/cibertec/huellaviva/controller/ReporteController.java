package com.cibertec.huellaviva.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private DataSource dataSource;

    // REPORTE 1: HISTORIAL DE ACTAS DE ADOPCIÓN Y TRAZABILIDAD
    @GetMapping("/adopciones")
    public void exportarReporteAdopciones(HttpServletResponse response, HttpSession session) throws Exception {
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("/login");
            return;
        }
        generarPdf("reportes/reporte_adopciones.jrxml", "Reporte_Adopciones_HuellaViva.pdf", response);
    }

    // REPORTE 2: INVENTARIO VALORIZADO DE INSUMOS Y ALERTAS DE STOCK
    @GetMapping("/inventario")
    public void exportarReporteInventario(HttpServletResponse response, HttpSession session) throws Exception {
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("/login");
            return;
        }
        generarPdf("reportes/reporte_inventario.jrxml", "Reporte_Inventario_HuellaViva.pdf", response);
    }

    private void generarPdf(String rutaJrxml, String nombreArchivo, HttpServletResponse response) throws Exception {
        try (Connection conexion = dataSource.getConnection();
             InputStream streamReporte = new ClassPathResource(rutaJrxml).getInputStream()) {

            JasperReport jasperReport = JasperCompileManager.compileReport(streamReporte);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TITULO_INSTITUCION", "REFUGIO HUELLA VIVA - SISTEMA DE TRAZABILIDAD");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conexion);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + nombreArchivo);

            OutputStream out = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();
        }
    }
}