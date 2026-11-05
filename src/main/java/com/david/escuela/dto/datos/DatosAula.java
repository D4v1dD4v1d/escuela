package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Datos de un aula")
public record DatosAula(
        @Schema(description = "Nombre del aula")
        String nombre,
        @Schema(description = "Capacidad de estudiantes que tiene el aula")
        Integer capacidad
) {}
