package com.david.escuela.dto.grupos;

import com.david.escuela.dto.datos.DatosAula;
import com.david.escuela.dto.datos.DatosCurso;
import com.david.escuela.dto.datos.DatosHorarios;
import com.david.escuela.dto.datos.DatosMaestro;
import com.david.escuela.entities.Aula;
import com.david.escuela.entities.Curso;
import com.david.escuela.entities.Horario;
import com.david.escuela.entities.Maestro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Información de un grupo")
public record GrupoResponse(
        @Schema(description = "ID del grupo", example = "1")
        Long id,
        @Schema(description = "Curso que toma el grupo")
        DatosCurso curso,
        @Schema(description = "Maestro del grupo")
        DatosMaestro maestro,
        @Schema(description = "Aula del grupo")
        DatosAula aula,
        @Schema(description = "Horario del grupo")
        List<String> horarios,
        @Schema(description = "Periodo del grupo")
        String periodo
) {}
