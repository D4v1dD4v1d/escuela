package com.david.escuela.dto.inscripciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para registrar o actualizar una inscripción")
public record InscripcionRequest(
        @Schema(description = "ID del grupo que tiene ese horario", example = "5")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID es un entero positivo")
        Long idAlumno,
        @Schema(description = "ID del grupo que tiene ese horario", example = "5")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID es un entero positivo")
        Long idGrupo
) {}
