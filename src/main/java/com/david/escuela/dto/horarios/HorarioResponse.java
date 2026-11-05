package com.david.escuela.dto.horarios;

import com.david.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un horario")

public record HorarioResponse(
    @Schema(description = "ID del horario", example = "1")
    Long id,
    @Schema(description = "Datos del grupo que tiene el horario")
    DatosGrupo grupo,
    @Schema(description = "Horario que tiene el grupo", example = "Lunes 08:00 10:00")
    String horario
) {}
