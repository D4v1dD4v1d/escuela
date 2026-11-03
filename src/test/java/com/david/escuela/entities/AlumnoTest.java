package com.david.escuela.entities;

import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class AlumnoTest {
    //actualizar
    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido(){
        //arrange
        Alumno alumno = Alumno.builder()
                .nombre("David")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Zavala")
                .email("david@david.com")
                .build();
        //act
        alumno.actualizar(
                "nombre",
                "paterno",
                "materno",
                "test@test");
        //assert
        assertThat(alumno.getNombre()).isEqualTo("nombre");
        assertThat(alumno.getApellidoPaterno()).isEqualTo("paterno");
        assertThat(alumno.getApellidoMaterno()).isEqualTo("materno");
        assertThat(alumno.getEmail()).isEqualTo("test@test");
    }
    @Test
    void actualizar_debeLanzarExecpcion_CuandoElNombreEsMuyCorto(){
        //arrange
        Alumno alumno = Alumno.builder()
                .nombre("David")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Zavala")
                .email("david@david.com")
                .build();

        //act y assert
        assertThatThrownBy(() -> alumno.actualizar(
                null, "paterno","materno", "test@test.com"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("El nombre es requerido");
    }
    //inscripciones
    @Test
    void inscribir_debeLanzarExcepcion_CuandoLaInscripcionEsNula(){
        //arrange
        Alumno alumno = Alumno.builder()
                .nombre("David")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Zavala")
                .email("david@david.com")
                .build();
        //act y assert
        assertThatThrownBy(()->alumno.inscribir(null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("La inscripción es requerida.");
    }
    @Test
    void inscribir_debeAgregarInscripcionALaListaDeInscripciones_CuandoLaInscripcionNoEsNula(){
        //arrange
        Alumno alumno = Alumno.builder()
                .nombre("David")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Zavala")
                .email("david@david.com")
                .build();
        Inscripcion inscripcion = Inscripcion.builder()
                        .alumno(alumno)
                        .grupo(new Grupo())
                        .calificacion(new Calificacion()).build();
        //act y assert
        alumno.inscribir(inscripcion);
        assertThat(alumno.getInscripciones().contains(inscripcion));
    }
    //crear
    @Test
    void crear_DebeCrearUnAlumno_SiLosDatosSonValidos(){
        Alumno alumno = Alumno.crear("David","Pérez","Zavala");
        assertThat(alumno.getNombre().equalsIgnoreCase("David"));

    }

    @Test
    void crear_DebeLanzarUnaExepcion_SiNombreEsNulo(){
        assertThatThrownBy(()->Alumno.crear(null,"Pérez","Zavala"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("El nombre es requerido");    }

}
