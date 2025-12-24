package com.vitaly.reportes.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    public ReporteController() {
        // Esta configuración desactiva la validación estricta de XML (corrige el error del uuid)
        // Se coloca en el constructor para que se ejecute al iniciar el controlador.
        DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
        context.setProperty("net.sf.jasperreports.compiler.xml.validation", "false");
    }

    @PostMapping("/generar")
    public ResponseEntity<byte[]> generarReporte(@RequestBody Models.ReportData data) {
        try {
            // 1. Cargar el archivo .jrxml desde resources
            InputStream reportStream = getClass().getResourceAsStream("/reportes/vitaly_reporte.jrxml");
            
            if (reportStream == null) {
                return ResponseEntity.internalServerError().body("No se encontró el archivo .jrxml".getBytes());
            }

            // 2. Compilar el reporte (Ahora sin validar el uuid)
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 3. Preparar los datos (DataSource)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data.getProductos());

            // 4. Parámetros del reporte (Encabezado, totales, etc.)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nombreCliente", data.getNombreCliente());
            parameters.put("fecha", data.getFecha());
            parameters.put("total", data.getTotal());

            // 5. Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // 6. Exportar a PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // 7. Configurar cabeceras de respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "reporte_vitaly.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error al generar el reporte: " + e.getMessage()).getBytes());
        }
    }
}