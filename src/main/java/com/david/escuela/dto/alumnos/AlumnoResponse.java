package com.david.escuela.dto.alumnos;

import com.david.escuela.dto.datos.DatosCalificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Información de un alumno")
public record AlumnoResponse(
        @Schema(description = "ID del alumno", example = "1")
        Long id,

        @Schema(description = "Nombre completo del alumno", example = "David Pérez Zavala")
        String nombre,

        @Schema(description = "Email del alumno", example = "test@test.com")
        String email,

        @Schema(description = "Matrícula del alumno", example = "test@test.com")
        String matricula,

        @Schema(description = "Fecha de ingreso", example = "10/01/2025")
        String fechaRegistro,

        @Schema(description = "Calificaciones del alumno")
        List<DatosCalificacion> calificaciones,

        @Schema(description = "Promedio del alumno", example = "8.9")
        BigDecimal promedio

){}
