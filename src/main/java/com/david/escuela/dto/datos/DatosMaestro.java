package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un Maestro")
public record DatosMaestro(
        @Schema(description = "Nombre completo del maestro")
        String nombre,
        @Schema(description = "Email del maestro")
        String email,
        @Schema(description = "Teléfono del maestro")
        String telefono
) {}
