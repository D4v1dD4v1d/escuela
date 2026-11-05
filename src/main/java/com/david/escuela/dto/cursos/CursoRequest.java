package com.david.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar un aula")
public record CursoRequest(
        @Schema(description = "Nombre del curso", example = "Matemáticas Discretas I")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1,max = 100,message = "El nombre debe contener entre 1 y 100 carateres")
        String nombre,
        @Schema(description = "Texto descriptivo de lo que trata el curso", example = "Curso introductivo de matemáticas discretas para alumos de nuevo ingreso.")
        @Size(min = 1,max = 200,message = "El nombre debe contener entre 8 y 100 carateres")
        String descripcion,
        @Schema(description = "Créditos del curso, debe ser un número entre 1 y 99", example = "5")
        @NotNull(message = "Los créditos son requeridos")
        @Min(value = 1, message = "Debe tener al menos un crédito")
        @Max(value = 99, message = "No puede tener más de 99 créditos")
        Integer creditos
) {}
