package com.david.escuela.entities;

import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class HorarioTest {
    @Test
    void crear_horaInicioMenorQueHoraFin_retornaHorario() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        assertThat(horario).isNotNull();
        assertThat(horario.getHoraInicio()).isEqualTo("08:00");
    }

    @Test
    void crear_horaInicioMayorOIgualQueHoraFin_lanzaDatoInvalidoException() {
        assertThatThrownBy(() -> Horario.crear("Lunes", "11:00", "10:00"))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void actualizar_rangoValido_actualizaHorario() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        horario.actualizar(grupo, "Martes", "10:00", "12:00");
        assertThat(horario.getHoraInicio()).isEqualTo("10:00");
        assertThat(horario.getGrupo()).isEqualTo(grupo);
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        assertThatThrownBy(() -> horario.actualizar(grupo, "Martes", "13:00", "12:00"))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void formatoHora_datosValidos_retornaCadenaFormateada() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        String resultado = horario.formatoHora("Lunes", "08:00", "10:00");
        assertThat(resultado).isNotBlank();
    }

    @Test
    void formatoHora_diaInvalido_lanzaExcepcion() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        assertThatThrownBy(() -> horario.formatoHora("Invalido", "08:00", "10:00"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void formatoHoraParaGrupo_datosValidos_retornaCadenaFormateada() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        String resultado = horario.formatoHoraParaGrupo("Lunes", "08:00", "10:00");
        assertThat(resultado).contains("-");
    }

    @Test
    void formatoHoraParaGrupo_diaInvalido_lanzaExcepcion() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        assertThatThrownBy(() -> horario.formatoHoraParaGrupo("Invalido", "08:00", "10:00"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void asignarGrupo_grupoValido_asignaGrupoCorrectamente() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        horario.asignarGrupo(grupo);
        assertThat(horario.getGrupo()).isEqualTo(grupo);
    }

    @Test
    void asignarGrupo_grupoNulo_noModificaGrupo() {
        Horario horario = Horario.crear("Lunes", "08:00", "10:00");
        horario.asignarGrupo(null);
        assertThat(horario.getGrupo()).isNull();
    }

    @Test
    void validarDatos_datosValidos_noHaceNada() {
        assertThatCode(() -> Horario.validarDatos("Lunes", "08:00", "10:00"))
                .doesNotThrowAnyException();
    }

    @Test
    void validarDatos_horaInicioVacia_lanzaDatoInvalidoException() {
        assertThatThrownBy(() -> Horario.validarDatos("Lunes", "", "10:00"))
                .isInstanceOf(DatoInvalidoException.class);
    }
}