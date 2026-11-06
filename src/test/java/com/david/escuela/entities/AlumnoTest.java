package com.david.escuela.entities;

import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class AlumnoTest {
    //crear
    @Test
    void crear_datosValidos_retornaAlumnoConfigurado() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        assertThat(alumno).isNotNull();
        assertThat(alumno.getNombre()).isEqualTo("David");
    }

    @Test
    void crear_nombreNulo_lanzaDatoInvalidoException() {
        assertThatThrownBy(() -> Alumno.crear(null, "Pérez", "Zavala"))
                .isInstanceOf(DatoInvalidoException.class);
    }
    //actualizar
    @Test
    void actualizar_datosValidos_modificaDatosCorrectamente() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        alumno.actualizar("Juan", "Gómez", "López", "juan@test.com", "1234567890");
        assertThat(alumno.getNombre()).isEqualTo("Juan");
        assertThat(alumno.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    void actualizar_nombreInvalido_lanzaDatoInvalidoException() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        assertThatThrownBy(() -> alumno.actualizar("", "Gómez", "López", "juan@test.com", "1234567890"))
                .isInstanceOf(DatoInvalidoException.class);
    }
    //asignar datos academicos
    @Test
    void asignarDatosAcademicos_datosValidos_asignaEmailYMatricula() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        alumno.asignarDatosAcademicos("test@test.com", "1234567890");
        assertThat(alumno.getEmail()).isEqualTo("test@test.com");
        assertThat(alumno.getMatricula()).isEqualTo("1234567890");
    }

    @Test
    void asignarDatosAcademicos_matriculaInvalida_lanzaDatoInvalidoException() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        assertThatThrownBy(() -> alumno.asignarDatosAcademicos("test@test.com", "123"))
                .isInstanceOf(DatoInvalidoException.class);
    }
    //obtener nombre completo
    @Test
    void obtenerNombreCompleto_nombresValidos_retornaNombreCompleto() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        String nombreCompleto = alumno.obtenerNombreCompleto();
        assertThat(nombreCompleto).isEqualTo("David Pérez Zavala");
    }



    @Test
    void obtenerPromedio_conNulosYValores_ignoraNulosPromedioSoloDeValoresNoNulos() {
        // arrange
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");

        // Inscripción con calificación nula
        Inscripcion inscripcionNula = Inscripcion.builder().calificacion(null).build();
        // Inscripción con calificación de 8
        Calificacion calificacion8 = Calificacion.builder().calificacion(BigDecimal.valueOf(8)).build();
        Inscripcion inscripcion8 = Inscripcion.builder().calificacion(calificacion8).build();
        // Inscripción con calificación de 10
        Calificacion calificacion10 = Calificacion.builder().calificacion(BigDecimal.valueOf(10)).build();
        Inscripcion inscripcion10 = Inscripcion.builder().calificacion(calificacion10).build();

        alumno.getInscripciones().add(inscripcionNula);
        alumno.getInscripciones().add(inscripcion8);
        alumno.getInscripciones().add(inscripcion10);

        // act
        BigDecimal promedio = alumno.obtenerPromedio();

        // assert (Promedio entre 8 y 10 debe ser 9.00)
        assertThat(promedio).isEqualByComparingTo(BigDecimal.valueOf(9.00).setScale(2));
    }

    @Test
    void obtenerPromedio_resultadoConDecimales_redondeaADosDecimalesHalfUp() {
        // arrange
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");

        // Calificaciones que dan un promedio con más decimales (ej. 25 / 3 = 8.333...)
        // Usaremos 8 y 8.35 o directamente un valor que genere redondeo HALF_UP como 8.335
        Calificacion cal1 = Calificacion.builder().calificacion(BigDecimal.valueOf(8.33)).build();
        Calificacion cal2 = Calificacion.builder().calificacion(BigDecimal.valueOf(8.34)).build();

        alumno.getInscripciones().add(Inscripcion.builder().calificacion(cal1).build());
        alumno.getInscripciones().add(Inscripcion.builder().calificacion(cal2).build());

        // act
        BigDecimal promedio = alumno.obtenerPromedio();

        // assert
        assertThat(promedio).isEqualByComparingTo(BigDecimal.valueOf(8.34).setScale(2));
    }

    @Test
    void obtenerPromedio_sinCalificaciones_retornaCero() {
        // arrange
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");

        // act
        BigDecimal promedio = alumno.obtenerPromedio();

        // assert
        assertThat(promedio).isEqualByComparingTo(BigDecimal.ZERO);
    }
    @Test
    void cambioEnDatos_datosDiferentes_retornaTrue() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        boolean cambio = alumno.cambioEnDatos("Juan", "Pérez", "Zavala");
        assertThat(cambio).isTrue();
    }
    @Test
    void cambioEnDatos_nombreInvalido_RetornaFalse() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        boolean cambio = alumno.cambioEnDatos("David", "Pérez", "Zavala");
        assertThat(cambio).isFalse();
    }

    @Test
    void cambioEnDatos_nombreInvalido_lanzaDatoInvalidoException() {
        Alumno alumno = Alumno.crear("David", "Pérez", "Zavala");
        assertThatThrownBy(() -> alumno.cambioEnDatos("", "Pérez", "Zavala"))
                .isInstanceOf(DatoInvalidoException.class);
    }
}