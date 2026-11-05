package com.david.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de horarios formateados")
public record DatosHorarios(
        @Schema(description = "Horario con día de la semana, hora de inicio y hora de fin")
        String horario
) {}
