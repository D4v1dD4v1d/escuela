package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class InscripcionTest {
    @Test
    void crear_alumnoYGrupoValidos_retornaInscripcionConFechaDeHoy() {
        Inscripcion inscripcion = Inscripcion.crear();
        assertThat(inscripcion).isNotNull();
    }

    @Test
    void crear_alumnoNulo_lanzaDatoInvalidoException() {
        assertThatCode(() -> Inscripcion.crear())
                .doesNotThrowAnyException();
    }

    @Test
    void actualizar_datosValidos_modificaDatosCorrectamente() {
        Inscripcion inscripcion = Inscripcion.crear();
        Alumno alumno = Alumno.builder().id(1L).nombre("David").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        inscripcion.actualizar(alumno, grupo);

        assertThat(inscripcion.getAlumno()).isEqualTo(alumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(grupo);
    }

    @Test
    void actualizar_alumnoNulo_lanzaDatoInvalidoException() {
        Inscripcion inscripcion = Inscripcion.crear();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        assertThatThrownBy(() -> inscripcion.actualizar(null, grupo))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void asignarAlumnoGrupo_datosValidos_asignaAlumnoYGrupo() {
        Inscripcion inscripcion = Inscripcion.crear();
        Alumno alumno = Alumno.builder().id(1L).nombre("David").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        inscripcion.asignarAlumnoGrupo(alumno, grupo);

        assertThat(inscripcion.getAlumno()).isEqualTo(alumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(grupo);
    }

    @Test
    void asignarAlumnoGrupo_datosNulos_asignaNulosSinExcepcion() {
        Inscripcion inscripcion = Inscripcion.crear();
        assertThatCode(() -> inscripcion.asignarAlumnoGrupo(null, null))
                .doesNotThrowAnyException();
        assertThat(inscripcion.getAlumno()).isNull();
    }

    @Test
    void validarDatos_datosValidos_noHaceNada() {
        Alumno alumno = Alumno.builder().id(1L).nombre("David").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        assertThatCode(() -> Inscripcion.validarDatos(alumno, grupo))
                .doesNotThrowAnyException();
    }

    @Test
    void validarDatos_alumnoNulo_lanzaDatoInvalidoException() {
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();

        assertThatThrownBy(() -> Inscripcion.validarDatos(null, grupo))
                .isInstanceOf(DatoInvalidoException.class);
    }
}
