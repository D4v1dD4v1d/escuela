package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class CalificacionTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange

        Calificacion calificacion = Calificacion.builder()
                .calificacion(BigDecimal.valueOf(9.6))
                .fechaRegistro(LocalDate.now())
                .inscripcion(new Inscripcion())
                .build();
        //act
        Inscripcion inscripcion =new Inscripcion();
        calificacion.actualizar(
                BigDecimal.valueOf(9.6),
                inscripcion
        );
        //assert
        assertThat(calificacion.getCalificacion()).isEqualTo(BigDecimal.valueOf(9.6));
        assertThat(calificacion.getInscripcion()).isEqualTo(inscripcion);

    }
    @Test
    void actualizar_debeLanzarExecpcion_CuandoLaInscripcionNoExiste(){
        //arrange
        Calificacion calificacion = Calificacion.builder()
                .calificacion(BigDecimal.valueOf(9.6))
                .fechaRegistro(LocalDate.now())
                .inscripcion(new Inscripcion())
                .build();
        //act and assert
        assertThatThrownBy(() -> calificacion.actualizar(BigDecimal.valueOf(9.6),null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La inscripción es requerida.");


    }
    //crear
    @Test
    void crear_DebeCrearUnAula_SiLosDatosSonValidos(){
        Inscripcion inscripcion =new Inscripcion();
        Calificacion calificacion = Calificacion.crear(BigDecimal.valueOf(5.6),inscripcion);

        assertThat(calificacion.getCalificacion().equals(BigDecimal.valueOf(5.6)));
        assertThat(calificacion.getInscripcion().equals(inscripcion));

    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiLaCalificacionEsNegativa(){
        Inscripcion inscripcion =new Inscripcion();
        assertThatThrownBy(()->Calificacion.crear(BigDecimal.valueOf(-3.2),inscripcion))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("La calificación es requerida y debe ser positiva");
    }
    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
       Calificacion.validarDatos(BigDecimal.valueOf(2.3),new Inscripcion());
    }

    void validarDatos_DebeLanzarExcepcion_SiLaCalificacionEsNula(){
        assertThatThrownBy(()->Calificacion.validarDatos(null,new Inscripcion()))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La calificación es requerida y debe ser positiva");
    }

}
