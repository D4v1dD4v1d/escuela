package com.david.escuela.dto.calificaciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CalificacionRequest(
        @Positive(message = "El id debe ser positivo")
        @NotNull(message = "El id de la inscripción es requerido")
        @Schema(description = "Id de la inscripción con la calificación asignada")
        Long idInscripcion,
        @Positive(message = "La calificación debe ser positiva")
        @NotNull(message = "La califiación es requerida")
        @Schema(description = "Calificación de la inscripción")
        BigDecimal calificacion
) {
}
