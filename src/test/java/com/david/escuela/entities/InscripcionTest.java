package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class InscripcionTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan Pérez").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        Alumno nuevoAlumno = Alumno.builder().id(2L).nombre("María López").build();
        Grupo nuevoGrupo = Grupo.builder().id(2L).periodo("2026-2").build();
        Calificacion nuevaCalificacion = Calificacion.builder().id(2L).build();

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .grupo(grupo)
                .calificacion(calificacion)
                .build();

        //act
        inscripcion.actualizar(
                nuevoAlumno,
                nuevoGrupo,
                nuevaCalificacion
        );

        //assert
        assertThat(inscripcion.getAlumno()).isEqualTo(nuevoAlumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(nuevoGrupo);
        assertThat(inscripcion.getCalificacion()).isEqualTo(nuevaCalificacion);
    }

    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsNull(){
        //arrange
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan Pérez").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .grupo(grupo)
                .calificacion(calificacion)
                .build();

        //act and assert
        assertThatThrownBy(() -> inscripcion.actualizar(null, grupo, calificacion))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El alumno es requerido.");
    }

    //crear
    @Test
    void crear_DebeCrearUnCurso_SiLosDatosSonValidos(){
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan Pérez").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo, calificacion);

        assertThat(inscripcion.getAlumno()).isEqualTo(alumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(grupo);
        assertThat(inscripcion.getCalificacion()).isEqualTo(calificacion);
    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiElCursioEsNulo(){
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        assertThatThrownBy(() -> Inscripcion.crear(null, grupo, calificacion))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El alumno es requerido.");
    }

    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan Pérez").build();
        Grupo grupo = Grupo.builder().id(1L).periodo("2026-1").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        Inscripcion.validarDatos(alumno, grupo, calificacion);
    }

    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan Pérez").build();
        Calificacion calificacion = Calificacion.builder().id(1L).build();

        assertThatThrownBy(() -> Inscripcion.validarDatos(alumno, null, calificacion))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El grupo es requerido.");
    }
}
