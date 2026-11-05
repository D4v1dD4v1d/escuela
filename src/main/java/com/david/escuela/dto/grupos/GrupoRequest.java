package com.david.escuela.dto.grupos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Información para registrar o actualizar un grupo")
public record GrupoRequest(
        @Schema(description = "ID del curso tomado por el grupo", example = "1")
        @NotNull(message = "El id del curso es requerido")
        @Positive(message = "El ID es un entero positivo")
        Long idCurso,
        @Schema(description = "ID del maestro del grupo", example = "10")
        @NotNull(message = "El id del maestro es requerido")
        Long idMaestro,
        @Schema(description = "ID del aula del grupo", example = "5")
        @NotNull(message = "El id del aula es requerido")
        Long idAula,
        @Schema(description = "Periodo del grupo", example = "2026-01")
        @NotBlank(message = "El periodo es requerido")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
                message = "El periodo debe tener el formato YYYY-MM, por ejemplo 2026-01")
        String periodo
) {}
