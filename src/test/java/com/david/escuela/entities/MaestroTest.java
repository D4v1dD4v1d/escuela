package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MaestroTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Maestro maestro = Maestro.builder()
                .id(1L)
                .nombre("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .email("juan.perez@email.com")
                .telefono("1234567890")
                .build();

        //act
        maestro.actualizar(
                "Carlos",
                "López",
                "Martínez",
                "carlos.lopez@email.com",
                "0987654321"
        );

        //assert
        assertThat(maestro.getNombre()).isEqualTo("Carlos");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("López");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Martínez");
        assertThat(maestro.getEmail()).isEqualTo("carlos.lopez@email.com");
        assertThat(maestro.getTelefono()).isEqualTo("0987654321");
    }

    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsNull(){
        //arrange
        Maestro maestro = Maestro.builder()
                .id(1L)
                .nombre("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .email("juan.perez@email.com")
                .telefono("1234567890")
                .build();

        //act and assert
        assertThatThrownBy(() -> maestro.actualizar(null, "Pérez", "Gómez", "juan.perez@email.com", "1234567890"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre es requerido y debe tener entre 1 y 50 caracteres");
    }

    //crear
    @Test
    void crear_DebeCrearUnCurso_SiLosDatosSonValidos(){
        Maestro maestro = Maestro.crear(
                "Juan",
                "Pérez",
                "Gómez",
                "juan.perez@email.com",
                "1234567890"
        );

        assertThat(maestro.getNombre()).isEqualTo("Juan");
        assertThat(maestro.getEmail()).isEqualTo("juan.perez@email.com");
        assertThat(maestro.getTelefono()).isEqualTo("1234567890");
    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiElCursioEsNulo(){
        assertThatThrownBy(() -> Maestro.crear(null, "Pérez", "Gómez", "juan.perez@email.com", "1234567890"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre es requerido y debe tener entre 1 y 50 caracteres");
    }

    //validar datos
    @Test
    void validarDatos_SiLosDatosSonValidos_NoHaceNada(){
        // Nota: Se invoca indirectamente a través de crear/actualizar ya que validarDatos es private en esta entidad.
        Maestro maestro = Maestro.crear(
                "Juan",
                "Pérez",
                "Gómez",
                "juan.perez@email.com",
                "1234567890"
        );
        assertThat(maestro).isNotNull();
    }

    @Test
    void validarDatos_DebeLanzarExcepcion_SiElNombreEsPequeño(){
        assertThatThrownBy(() -> Maestro.crear("", "Pérez", "Gómez", "juan.perez@email.com", "1234567890"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre es requerido y debe tener entre 1 y 50 caracteres");
    }
}
