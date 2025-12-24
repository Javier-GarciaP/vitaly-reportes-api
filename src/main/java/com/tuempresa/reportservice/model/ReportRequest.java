package com.tuempresa.model;

import lombok.Data;
import java.util.Map;

@Data // Esto genera automáticamente Getters, Setters y toString
public class ReportRequest {
    private String tipoReporte;
    private String nombre;
    private String apellido;
    private String cedula;
    private String edad;
    private Map<String, Object> datos;
}