package com.david.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información del curso.")
public record CursoResponse(
        @Schema(description = "ID del curso", example = "1")
        Long id,
        @Schema(description = "Nombre del curso", example = "Matemáticas Discretas I")
        String nombre,
        @Schema(description = "Descripción del curso", example = "Curso introductivo de matemáticas discretas para alumos de nuevo ingreso.")
        String descripcion,
        @Schema(description = "Créditos del curso, debe ser un número entre 1 y 99", example = "5")
        Integer creditos
) {}
