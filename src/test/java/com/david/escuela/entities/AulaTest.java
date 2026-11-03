package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class AulaTest {
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Aula aula = Aula.builder()
                .nombre("Aula 300")
                .capacidad(20)
                .build();
        //act
        aula.actualizar(
                "Aula 252",
                50
        );
        //assert
        assertThat(aula.getNombre()).isEqualTo("Aula 252");
        assertThat(aula.getCapacidad()).isEqualTo(50);

    }
    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsMuyCorto(){
        //arrange
        Aula aula = Aula.builder()
                .nombre("Aula 300")
                .capacidad(20)
                .build();
        //act and assert
        assertThatThrownBy(() -> aula.actualizar("Aula 300",-9))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La capacidad debe ser un entero positivo");


    }

    @Test
    void crear_DebeCrearUnAula_SiLosDatosSonValidos(){
        Aula aula = Aula.crear("Aula 523",23);
        assertThat(aula.getNombre().equalsIgnoreCase("Aula 523"));
        assertThat(aula.getCapacidad().equals(23));

    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiLaCapacidadEsNegativa(){
        assertThatThrownBy(()->Aula.crear("Aula 523",-23))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("La capacidad debe ser un entero positivo");    }
    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        Aula.crear("Laboratorio de Cómputo 1", 30);
    }

    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        assertThatThrownBy(() -> Aula.crear("Aula 1", 30))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre es requerido y debe tener entre 8 y 100 caracteres");
    }
}
