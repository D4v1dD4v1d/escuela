package com.david.escuela.dto.inscripciones;

import com.david.escuela.dto.datos.DatosAlumno;
import com.david.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Información de una inscripción")
public record InscripcionResponse(
        @Schema(description = "Id de la inscripción")
        Long id,
        @Schema(description = "Datos del alumno")
        DatosAlumno alumno,
        @Schema(description = "Datos del grupo")
        DatosGrupo grupo,
        @Schema(description = "Calificación de la inscripción")
        BigDecimal calificacion,
        @Schema(description = "Fecha de la inscripción")
        String fechaInscripcion
) {}
