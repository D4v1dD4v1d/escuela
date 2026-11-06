package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MaestroTest {
    //crear
    @Test
    void crear_datosValidos_retornaMaestroConDatosSinEspacios() {
        // arrange & act: pasamos espacios al inicio y final para verificar que se limpien (trim)
        Maestro maestro = Maestro.crear("  Juan  ", "  Pérez  ", "  Gómez  ", "  JUAN@TEST.COM  ", " 1234567890 ");

        // assert
        assertThat(maestro).isNotNull();
        assertThat(maestro.getNombre()).isEqualTo("Juan");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Gómez");
        assertThat(maestro.getEmail()).isEqualTo("juan@test.com");
        assertThat(maestro.getTelefono()).isEqualTo("1234567890");
    }

    @Test
    void crear_emailVacio_lanzaDatoInvalidoException() {
        // arrange & act & assert: el segundo parámetro vacío (o inválido para el email según tu validación de tamaño 8-100) debe lanzar la excepción
        assertThatThrownBy(() -> Maestro.crear("Juan", "Pérez", "Gómez", "", "1234567890"))
                .isInstanceOf(DatoInvalidoException.class);
    }
    // actualizar
    @Test
    void actualizar_datosValidos_modificaDatosCorrectamente() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@test.com", "1234567890");
        maestro.actualizar("Carlos", "López", "Martínez", "carlos@test.com", "0987654321");

        assertThat(maestro.getNombre()).isEqualTo("Carlos");
        assertThat(maestro.getEmail()).isEqualTo("carlos@test.com");
        assertThat(maestro.getTelefono()).isEqualTo("0987654321");
    }

    @Test
    void actualizar_emailInvalido_lanzaDatoInvalidoException() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@test.com", "1234567890");

        assertThatThrownBy(() -> maestro.actualizar("Carlos", "López", "Martínez", "", "0987654321"))
                .isInstanceOf(DatoInvalidoException.class);
    }
    //obtener nombre completo
    @Test
    void obtenerNombreCompleto_nombresValidos_retornaNombreCompleto() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@test.com", "1234567890");
        String nombreCompleto = maestro.obtenerNombreCompleto();

        assertThat(nombreCompleto).isEqualTo("Juan Pérez Gómez");
    }

    @Test
    void obtenerNombreCompleto_camposNulos_retornaTextoConNulls() {
        Maestro maestro = Maestro.builder().build();
        String nombreCompleto = maestro.obtenerNombreCompleto();

        assertThat(nombreCompleto).isEqualTo("null null null");
    }
}
