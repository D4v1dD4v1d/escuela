package com.david.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesrios para registrar o actualizar un aula")
public record AulaRequest(
        @Schema(description = "Nombre del aula", example = "Aula 101")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1,max = 100,message = "El nombre debe contener entre 1 y 100 carateres")
        String nombre,
        @Schema(description = "Capacidad de alumnos entre 1 y 40", example = "30")
        @NotNull(message = "La capacidad es requerida")
        @Min(value = 1, message = "Debe tener capacidad para al menos 1 alumno")
        @Max(value = 40, message = "No puede tener capacidad para más de 40 alumnos")
        Integer capacidad
) {
}
