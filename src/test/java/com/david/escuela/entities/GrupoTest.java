package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class GrupoTest {
    @Test
    void actualizar_datosValidos_actualizaCampos() {
        Grupo grupo = Grupo.crear("2026-1");
        Curso curso = Curso.builder().id(1L).build();
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        grupo.actualizar(curso, maestro, aula, "2026-2");

        assertThat(grupo.getPeriodo()).isEqualTo("2026-2");
        assertThat(grupo.getCurso()).isEqualTo(curso);
    }

    @Test
    void actualizar_periodoVacio_lanzaDatoInvalidoException() {
        Grupo grupo = Grupo.crear("2026-1");
        Curso curso = Curso.builder().id(1L).build();
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        // Se pasa un periodo vacío ("") para coincidir con el nombre del test
        assertThatThrownBy(() -> grupo.actualizar(curso, maestro, aula, ""))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void crear_datosValidos_creaCampos() {
        Grupo grupo = Grupo.crear("2026-1");
        assertThat(grupo).isNotNull();
        assertThat(grupo.getPeriodo()).isEqualTo("2026-1");
    }

    @Test
    void crear_periodoNulo_lanzaDatoInvalidoException() {
        // Se pasa un valor nulo (null) para que coincida exactamente con "periodoNulo"
        assertThatThrownBy(() -> Grupo.crear(null))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void validarDatos_datosValidos_noHaceNada() {
        Curso curso = Curso.builder().id(1L).build();
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        assertThatCode(() -> Grupo.validarDatos(curso, maestro, aula))
                .doesNotThrowAnyException();
    }

    @Test
    void validarDatos_cursoNulo_lanzaDatoInvalidoException() {
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        assertThatThrownBy(() -> Grupo.validarDatos(null, maestro, aula))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void validarPeriodo_periodoValido_noHaceNada() {
        assertThatCode(() -> Grupo.validarPeriodo("2026-1"))
                .doesNotThrowAnyException();
    }

    @Test
    void validarPeriodo_periodoVacio_lanzaDatoInvalidoException() {
        assertThatThrownBy(() -> Grupo.validarPeriodo(""))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void asignarObjetos_objetosValidos_asignaCorrectamente() {
        Grupo grupo = Grupo.crear("2026-1");
        Curso curso = Curso.builder().id(1L).build();
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        grupo.asignarObjetos(maestro, curso, aula);

        assertThat(grupo.getCurso()).isEqualTo(curso);
        assertThat(grupo.getMaestro()).isEqualTo(maestro);
        assertThat(grupo.getAula()).isEqualTo(aula);
    }

    @Test
    void asignarObjetos_cursoNulo_lanzaDatoInvalidoException() {
        Grupo grupo = Grupo.crear("2026-1");
        Maestro maestro = Maestro.builder().id(1L).build();
        Aula aula = Aula.builder().id(1L).build();

        assertThatThrownBy(() -> grupo.asignarObjetos(maestro, null, aula))
                .isInstanceOf(DatoInvalidoException.class);
    }


}
