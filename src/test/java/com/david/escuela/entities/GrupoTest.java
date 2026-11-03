package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class GrupoTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Curso curso = Curso.builder().id(1L).nombre("Matemáticas discretas I").creditos(10).build();
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        Curso nuevoCurso = Curso.builder().id(2L).nombre("Física I").creditos(8).build();
        Maestro nuevoMaestro = Maestro.builder().id(2L).nombre("María López").build();
        Aula nuevaAula = Aula.builder().id(2L).nombre("Aula 102").build();

        Grupo grupo = Grupo.builder()
                .curso(curso)
                .maestro(maestro)
                .aula(aula)
                .periodo("2026-1")
                .build();

        //act
        grupo.actualizar(
                nuevoCurso,
                nuevoMaestro,
                nuevaAula,
                "2026-2"
        );

        //assert
        assertThat(grupo.getCurso()).isEqualTo(nuevoCurso);
        assertThat(grupo.getMaestro()).isEqualTo(nuevoMaestro);
        assertThat(grupo.getAula()).isEqualTo(nuevaAula);
        assertThat(grupo.getPeriodo()).isEqualTo("2026-2");
    }

    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsNull(){
        //arrange
        Curso curso = Curso.builder().id(1L).nombre("Matemáticas discretas I").creditos(10).build();
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        Grupo grupo = Grupo.builder()
                .curso(curso)
                .maestro(maestro)
                .aula(aula)
                .periodo("2026-1")
                .build();

        //act and assert
        assertThatThrownBy(() -> grupo.actualizar(curso, maestro, aula, null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El periodo es requerido y debe contener entre 5 y 10 caracteres.");
    }

    //crear
    @Test
    void crear_DebeCrearUnCurso_SiLosDatosSonValidos(){
        Curso curso = Curso.builder().id(1L).nombre("Matemáticas discretas I").creditos(10).build();
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        Grupo grupo = Grupo.crear(curso, maestro, aula);

        assertThat(grupo.getCurso()).isEqualTo(curso);
        assertThat(grupo.getMaestro()).isEqualTo(maestro);
        assertThat(grupo.getAula()).isEqualTo(aula);
    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiElCursioEsNulo(){
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        assertThatThrownBy(() -> Grupo.crear(null, maestro, aula))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El curso es requerido.");
    }

    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        Curso curso = Curso.builder().id(1L).nombre("Matemáticas discretas I").creditos(10).build();
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        Grupo.validarDatos(curso, maestro, aula, "2026-1");
    }

    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        Curso curso = Curso.builder().id(1L).nombre("Matemáticas discretas I").creditos(10).build();
        Maestro maestro = Maestro.builder().id(1L).nombre("Juan Pérez").build();
        Aula aula = Aula.builder().id(1L).nombre("Aula 101").build();

        assertThatThrownBy(() -> Grupo.validarDatos(curso, maestro, aula, "202"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El periodo es requerido y debe contener entre 5 y 10 caracteres.");
    }

}
