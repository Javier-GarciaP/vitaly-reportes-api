package com.vitaly.reportes.dto; // Agregamos .dto al final

import lombok.Data;
import java.util.List;

/**
 * CLASE BASE: Contiene lo que todos los reportes comparten
 */
@Data
class BaseReporte {
    private String nombre;
    private String apellido;
    private String paciente; // <--- Agrega esta línea
    private String cedula;
    private String edad;
}

/**
 * Reportes de una sola página (Simples)
 */
@Data public class ReporteQuimica extends BaseReporte {
    private String glicemia, urea, creatinina, colesterol, colestHDL, trigliceridos, 
                   acUrico, calcio, fosforo, proteinasTot, albumina, globulinas, 
                   relacionAG, tGO, tGP, bilirrTotal, bilirrDirecta, bilirrIndirecta, 
                   fosfAlc, lDH, amilasa, observacion;
}

@Data public class ReporteOrina extends BaseReporte {
    private String aspecto, color, reaccion, ph, densidad, proteina, hemoglobina, 
                   glucosa, urobilinogeno, nitritos, acetona, pigmentosBiliares, 
                   celulasEpiteliales, filMoco, leucocitos, cilindros, hematies, 
                   cristales, bacterias, levaduras, observaciones;
}

@Data public class ReporteHematologia extends BaseReporte {
    private String hematies, hemoglobina, hematocrito, vcm, hcm, chcm, 
                   recuentoLeucocitos, neutrofilos, linfocitos, monocitos, 
                   eosinofilos, basofilos, recuentoPlaquetas, unoHora, 
                   dosHora, indice, observaciones;
}

@Data public class ReporteHeces extends BaseReporte {
    private String aspecto, consistencia, hb, ph, color, moco, reaccion, 
                   azReductores, polimorfonucleares, sangreOculta, 
                   restosAlimenticios, floraBacteriana, parasitos, observaciones;
}

@Data public class ReporteBacteriologia extends BaseReporte {
    private String observacion, tincion, recuento, cultivo, cultivoHongos, muestra;
}

@Data public class ReporteMiscelaneos extends BaseReporte {
    private String examenSolicitado, metodo, muestra, resultado;
}

@Data public class ReporteCoagulacion extends BaseReporte {
    private String control1, actP, inr, paciente1, razon, isi, control2, 
                   paciente2, fibrinogeno;
}

@Data public class ReporteGrupoSanguineo extends BaseReporte {
    private String grupoSanguineo, factorRH;
}

/**
 * Reporte de Antibiograma (Con Tabla Dinámica)
 */
@Data public class ReporteAntibiogramaDTO extends BaseReporte {
    private String germenSelccA;
    private String germenSelccB;
    private List<FilaAntibiotico> data; // Aquí van las filas de la tabla
}

@Data public class FilaAntibiotico {
    private String antibiotico;
    private String itemA;
    private String itemB;
}