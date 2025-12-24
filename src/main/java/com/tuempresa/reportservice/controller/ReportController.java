package com.tuempresa.reportservice.controller;

import com.tuempresa.reportservice.model.ReportRequest;
import com.tuempresa.reportservice.service.JasperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private JasperService jasperService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateReport(@RequestBody ReportRequest request) {
        try {
            // Llamamos al servicio para obtener los bytes del PDF
            byte[] reportBytes = jasperService.exportToPdf(request);

            // Configuramos las cabeceras para que el navegador lo reconozca como PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            // Esto permite que se abra en el navegador o se descargue
            String fileName = request.getTipoReporte() + "_" + request.getCedula() + ".pdf";
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}