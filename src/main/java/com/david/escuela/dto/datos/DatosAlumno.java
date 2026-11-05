package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un alumno")
public record DatosAlumno(
        @Schema(description = "Nombre completo del alumno")
           String nombre,
        @Schema(description = "Matrícula del alumno")
           String matricula,
        @Schema(description = "Email del alumno")
           String email,
        @Schema(description = "Fecha en que ingreso a la institución")
           String fechaIngreso
) {}
