package com.vitaly.reportes.controller;

import com.vitaly.reportes.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*") // Permite la conexión con tu App Next.js
public class ReporteController {

    @PostMapping("/generar/{tipo}")
    public ResponseEntity<byte[]> generarReporte(@PathVariable String tipo, @RequestBody Map<String, Object> payload) {
        try {
            // 1. Cargar el binario .jasper desde resources/reports/
            // Se recomienda que los archivos estén en minúsculas en el servidor (ej: quimica.jasper)
            InputStream reportStream = getClass().getResourceAsStream("/reports/" + tipo.toLowerCase() + ".jasper");
            
            if (reportStream == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 2. Unificar Nombre y Apellido en un campo "paciente"
            // Esto permite que $F{paciente} en Jasper funcione correctamente
            String nombre = payload.getOrDefault("nombre", "").toString();
            String apellido = payload.getOrDefault("apellido", "").toString();
            payload.put("paciente", (nombre + " " + apellido).trim());

            // 3. PREPARAR PARÁMETROS (Aquí enviamos la imagen)
            Map<String, Object> parameters = new HashMap<>();
            
            // Obtenemos la URL del recurso de la imagen de forma dinámica
            var logoUrl = getClass().getResource("/recursos/vitalyLogo.png");
            if (logoUrl != null) {
                // Se la pasamos al reporte con el nombre del parámetro que creaste
                parameters.put("logoPath", logoUrl.toString());
            }

            // 4. Convertir el JSON recibido a la clase correspondiente usando Jackson
            ObjectMapper mapper = new ObjectMapper();
            Object dataInstance;
            
            // Lógica para elegir el molde de datos según el tipo de examen
            switch (tipo.toLowerCase()) {
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
                
                // CASOS DE PORTADAS (Reciben BaseReporte: nombre, apellido, cedula, edad)
                case "portadaninos":
                case "portadaadultos":
                case "portadaadolecentes":
                case "portadageneral":
                    dataInstance = mapper.convertValue(payload, BaseReporte.class);
                    break;

                default: 
                    dataInstance = payload; 
            }

            // 5. Crear el DataSource
            JRDataSource dataSource;
            if (tipo.equalsIgnoreCase("antibiograma")) {
                // El antibiograma usa la lista interna "data" para llenar la tabla
                ReporteAntibiogramaDTO dto = (ReporteAntibiogramaDTO) dataInstance;
                dataSource = new JRBeanCollectionDataSource(dto.getData());
            } else {
                // Los demás usan el objeto principal como una sola fila
                dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dataInstance));
            }

            // 6. Llenar el reporte y exportar a PDF
            // IMPORTANTE: Pasamos 'parameters' (que tiene el logoPath) en lugar de un HashMap vacío
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // 7. Responder con el archivo PDF al navegador
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                // Usamos inline para que el navegador lo abra directamente en lugar de descargarlo
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + tipo + ".pdf")
                .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}