package com.vitaly.reportes.controller;

import lombok.Data;
import java.util.List;

/**
 * Clase base con los datos comunes de todos los pacientes.
 */
@Data
class BaseReporte {
    private String nombre;
    private String apellido;
    private String paciente; // Se genera en el controlador uniendo nombre y apellido
    private String cedula;
    private String edad;
    private String fecha;
}

@Data
class ReporteQuimica extends BaseReporte {
    private String glicemia, urea, creatinina, colesterol, trigliceridos, acUrico, calcio, fosforo, tGO, tGP, observacion;
}

@Data
class ReporteOrina extends BaseReporte {
    private String aspecto, color, densidad, ph, proteinas, glucosa, cetonas, bilirrubina, sangre, nitritos, urobilinogeno;
    private String leucocitos, hematies, celulasEpiteliales, cristales, bacterias, cilindros, moco, observacion;
}

@Data
class ReporteHematologia extends BaseReporte {
    private String glubulosRojos, hemoglobina, hematocrito, vcm, hcm, chcm, plaquetas;
    private String leucocitos, segmentados, linfocitos, monocitos, eosinofilos, basofilos, observacion;
}

@Data
class ReporteHeces extends BaseReporte {
    private String color, consistencia, moco, sangre, restosAlimenticios, almidon, grasas;
    private String quistes, trofozoitos, larvas, huevos, observacion;
}

@Data
class ReporteBacteriologia extends BaseReporte {
    private String muestra, examenDirecto, gram, cultivo, resultado, observacion;
}

@Data
class ReporteCoagulacion extends BaseReporte {
    private String tiempoProtrombina, tiempoTromboplastina, fibrinogeno, tiempoSangria, tiempoCoagulacion, observacion;
}

@Data
class ReporteGrupoSanguineo extends BaseReporte {
    private String grupo, factorRh, observacion;
}

@Data
class ReporteMiscelaneos extends BaseReporte {
    private String prueba, resultado, valoresReferencia, observacion;
}

/**
 * Para el Antibiograma, necesitamos una lista de filas para la tabla.
 */
@Data
class ReporteAntibiogramaDTO extends BaseReporte {
    private String germenAislado, recuentoColonia, observacion;
    private List<FilaAntibiograma> data; // Esta es la lista que usa Jasper para la tabla
}

@Data
class FilaAntibiograma {
    private String antibiotico;
    private String sensible;
    private String resistente;
    private String intermedio;
}