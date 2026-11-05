package com.david.escuela.dto.horarios;

import com.david.escuela.enums.DiaSemana;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Información para registrar o actualizar un horario.")
public record HorarioRequest(
        @Schema(description = "ID del grupo que tiene ese horario", example = "5")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID es un entero positivo")
        Long idGrupo,
        @Schema(description = "Día de la semana del horario",example = "Miércoles")
        @NotNull(message = "El día es requerido")
        String dia,
        @Schema(description = "Hora en que inicia el curso", example = "08:00")
        @NotNull(message = "La hora inicial es requerida")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "La hora debe tener el formato HH:MM")
        String horaInicio,
        @Schema(description = "Hora en que termina el curso", example = "10:00")
        @NotNull(message = "La hora final es requerida")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$",
                message = "La hora debe tener el formato HH:MM")
        String horaFin
) {}
