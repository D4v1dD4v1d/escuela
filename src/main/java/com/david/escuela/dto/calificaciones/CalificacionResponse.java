package com.david.escuela.dto.calificaciones;


import com.david.escuela.dto.datos.DatosInscripcion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CalificacionResponse(
        @Schema(description = "ID de la calificación")
        Long id,
        @Schema(description = "Datos de la incripción")
        DatosInscripcion inscripcion,
        @Schema(description = "Calificación de la inscripción")
        BigDecimal calificacion,
        @Schema(description = "Fecha de registro de la calificación")
        String fechaRegistro
) {}
