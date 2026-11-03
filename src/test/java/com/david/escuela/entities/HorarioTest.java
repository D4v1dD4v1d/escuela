package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class HorarioTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Grupo nuevoGrupo = Grupo.builder().id(2L).periodo("2026-2").build();

        Horario horario = Horario.builder()
                .grupo(grupo)
                .diaSemana(com.david.escuela.enums.DiaSemana.LUNES)
                .horaIinicio("08:00")
                .horaFin("10:00")
                .build();

        //act
        horario.actualizar(
                nuevoGrupo,
                "Martes",
                "10:00",
                "12:00"
        );

        //assert
        assertThat(horario.getGrupo()).isEqualTo(nuevoGrupo);
        assertThat(horario.getDiaSemana().name()).isEqualTo("MARTES");
        assertThat(horario.getHoraIinicio()).isEqualTo("10:00");
        assertThat(horario.getHoraFin()).isEqualTo("12:00");
    }

    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsNull(){
        //arrange
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        Horario horario = Horario.builder()
                .grupo(grupo)
                .diaSemana(com.david.escuela.enums.DiaSemana.LUNES)
                .horaIinicio("08:00")
                .horaFin("10:00")
                .build();

        //act and assert
        assertThatThrownBy(() -> horario.actualizar(grupo, "Lunes", null, "10:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de inicio es requerida");
    }

    //crear
    @Test
    void crear_DebeCrearUnCurso_SiLosDatosSonValidos(){
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        Horario horario = Horario.crear(grupo, "Lunes", "08:00", "10:00");

        assertThat(horario.getGrupo()).isEqualTo(grupo);
        assertThat(horario.getHoraIinicio()).isEqualTo("08:00");
        assertThat(horario.getHoraFin()).isEqualTo("10:00");
    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiElCursioEsNulo(){
        assertThatThrownBy(() -> Horario.crear(null, "Lunes", "08:00", "10:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El grupo es requerido.");
    }

    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        Horario.validarDatos(grupo, "Lunes", "08:00", "10:00");
    }

    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        assertThatThrownBy(() -> Horario.validarDatos(grupo, "Lunes", "", "10:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de inicio es requerida");
    }
}
