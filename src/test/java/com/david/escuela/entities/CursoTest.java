package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class CursoTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange

        Curso curso = Curso.builder()
                .nombre("Matemáticas discretas I")
                .descripcion("")
                .creditos(10)
                .build();
        //act
        curso.actualizar(
                "Matemáticas discretas II",
                null,
                23
        );
        //assert
        assertThat(curso.getNombre()).isEqualTo("Matemáticas discretas II");
        assertThat(curso.getDescripcion()).isEqualTo(null);
        assertThat(curso.getCreditos()).isEqualTo(23);

    }
    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsNull(){
        //arrange
        Curso curso = Curso.builder()
                .nombre("Matemáticas discretas I")
                .descripcion("")
                .creditos(10)
                .build();
        //act and assert
        assertThatThrownBy(() -> curso.actualizar(null,null,20))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre del curso debe estar entre 10 y 100 caracteres y es requerido.");


    }
    //crear
    @Test
    void crear_DebeCrearUnCurso_SiLosDatosSonValidos(){
        Curso curso = Curso.crear("buiashdfjdsoadsjue",null,26);

        assertThat(curso.getNombre().equals("buiashdfjdsoadsjue"));
        assertThat(curso.getCreditos().equals(26));
    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiElCursioEsNulo(){
        assertThatThrownBy(()->Curso.crear("kldsnfklmdsklfml","Descripcion de prueba",null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("Debe tener al menos un crédito");
    }
    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        Curso.validarDatos("jhfjsedhjgkh",10);
    }
    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        assertThatThrownBy(()->Curso.validarDatos("jid",20))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre del curso debe estar entre 10 y 100 caracteres y es requerido.");
    }

}
