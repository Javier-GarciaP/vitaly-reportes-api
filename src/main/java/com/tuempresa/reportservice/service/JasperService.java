package com.tuempresa.reportservice.service;

import com.tuempresa.reportservice.model.ReportRequest;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.*;

@Service
public class JasperService {

    public byte[] exportToPdf(ReportRequest request) throws JRException {
        // 1. Cargar el archivo .jasper (debe estar en src/main/resources/reports/)
        String reportName = request.getTipoReporte().toLowerCase() + ".jasper";
        InputStream reportStream = getClass().getResourceAsStream("/reports/" + reportName);
        
        if (reportStream == null) {
            throw new JRException("No se encontró el archivo: " + reportName);
        }

        // 2. Configurar Parámetros (Ajustado a tu variable logoPath)
        Map<String, Object> parameters = new HashMap<>();
        
        // Cargamos la imagen desde resources
        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            // Aquí usamos el nombre exacto que tienes en tu Jasper
            parameters.put("logoPath", logoStream); 
        }

        // 3. Mapeo de campos (Fields)
        // Creamos un mapa con los datos básicos + los específicos del examen
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("nombre", request.getNombre() + " " + request.getApellido());
        fieldsMap.put("cedula", request.getCedula());
        fieldsMap.put("edad", request.getEdad());

        // Inyectamos automáticamente todos los campos que envíes en el JSON
        if (request.getDatos() != null) {
            fieldsMap.putAll(request.getDatos());
        }

        // Creamos la fuente de datos con una lista de un solo elemento (el mapa de campos)
        List<Map<String, ?>> dataList = new ArrayList<>();
        dataList.add(fieldsMap);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        // 4. Generar el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

        // 5. Exportar a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}