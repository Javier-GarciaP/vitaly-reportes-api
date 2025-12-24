package com.vitaly.reportes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*") 
public class ReporteController {

    @PostMapping("/generar/{tipo}")
    public ResponseEntity<byte[]> generarReporte(@PathVariable String tipo, @RequestBody Map<String, Object> payload) {
        try {
            // 1. CARGAR EL ARCHIVO FUENTE .jrxml
            // La URL define qué archivo buscar (ej: /generar/quimica -> quimica.jrxml)
            InputStream reportStream = getClass().getResourceAsStream("/reports/" + tipo.toLowerCase() + ".jrxml");
            
            if (reportStream == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 2. UNIFICAR NOMBRE Y APELLIDO (Para el campo 'paciente' en Jasper)
            String nombre = payload.getOrDefault("nombre", "").toString();
            String apellido = payload.getOrDefault("apellido", "").toString();
            payload.put("paciente", (nombre + " " + apellido).trim());

            // 3. PREPARAR PARÁMETROS (Logo)
            Map<String, Object> parameters = new HashMap<>();
            var logoUrl = getClass().getResource("/recursos/vitalyLogo.png");
            if (logoUrl != null) {
                parameters.put("logoPath", logoUrl.toString());
            }

            // 4. MAPEO DINÁMICO DE DATOS SEGÚN EL EXAMEN
            ObjectMapper mapper = new ObjectMapper();
            Object dataInstance;
            String examen = tipo.toLowerCase();

            if (examen.contains("portada")) {
                dataInstance = mapper.convertValue(payload, BaseReporte.class);
            } else {
                switch (examen) {
                    case "quimica":
                        dataInstance = mapper.convertValue(payload, ReporteQuimica.class);
                        break;
                    case "orina":
                        dataInstance = mapper.convertValue(payload, ReporteOrina.class);
                        break;
                    case "hematologia":
                        dataInstance = mapper.convertValue(payload, ReporteHematologia.class);
                        break;
                    case "heces":
                        dataInstance = mapper.convertValue(payload, ReporteHeces.class);
                        break;
                    case "bacteriologia":
                        dataInstance = mapper.convertValue(payload, ReporteBacteriologia.class);
                        break;
                    case "coagulacion":
                        dataInstance = mapper.convertValue(payload, ReporteCoagulacion.class);
                        break;
                    case "gruposanguineo":
                        dataInstance = mapper.convertValue(payload, ReporteGrupoSanguineo.class);
                        break;
                    case "miscelaneos":
                        dataInstance = mapper.convertValue(payload, ReporteMiscelaneos.class);
                        break;
                    case "antibiograma":
                        dataInstance = mapper.convertValue(payload, ReporteAntibiogramaDTO.class);
                        break;
                    default:
                        dataInstance = payload; // Mapa genérico si no coincide
                }
            }

            // 5. CONFIGURAR DATA SOURCE
            JRDataSource dataSource;
            if (examen.equals("antibiograma")) {
                // El antibiograma usa la lista interna 'data' para la tabla
                ReporteAntibiogramaDTO dto = (ReporteAntibiogramaDTO) dataInstance;
                dataSource = new JRBeanCollectionDataSource(dto.getData());
            } else {
                // Todos los demás envían el objeto como una única fila
                dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dataInstance));
            }

            // 6. COMPILACIÓN Y GENERACIÓN DEL PDF
            // Compilar JRXML -> JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Llenar Reporte -> JasperPrint
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Exportar a Bytes -> PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // 7. RETORNAR EL PDF AL NAVEGADOR
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + tipo + ".pdf")
                .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace(); // Esto aparecerá en los logs de Render si algo falla
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}