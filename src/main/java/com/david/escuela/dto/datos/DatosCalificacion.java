package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Datos de calificación")
public record DatosCalificacion(
        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        String nombre,
        @Schema(description = "Periodo que dura el curso", example = "2025-01")
        String periodo,
        @Schema(description = "Calificación del curso", example = "8.9")
        BigDecimal calificaion
){}
