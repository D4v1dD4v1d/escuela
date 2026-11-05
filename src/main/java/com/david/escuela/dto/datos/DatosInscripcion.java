package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

public record DatosInscripcion(
        @Schema(description = "Datos de un alumno")
        DatosAlumno alumno,
        @Schema(description = "Datos de un grupo")
        DatosGrupo grupo,
        @Schema(description = "Fecha de la inscripción")
        String fechaInscripcion
) {
}
