package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class CursoTest {
    @Test
    void crear_datosValidos_retornaCurso() {
        Curso curso = Curso.crear("Programación I", "Introducción a Java", 5);
        assertThat(curso).isNotNull();
        assertThat(curso.getNombre()).isEqualTo("Programación I");
        assertThat(curso.getCreditos()).isEqualTo(5);
    }

    @Test
    void crear_creditosNoPositivos_lanzaDatoInvalidoException() {
        // Se pasa un crédito no positivo (0) para que coincida con el nombre del test
        assertThatThrownBy(() -> Curso.crear("Programación I", "Descripción", 0))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void actualizar_datosValidos_actualizaCampos() {
        Curso curso = Curso.crear("Programación I", "Introducción a Java", 5);
        curso.actualizar("Programación Avanzada", "Spring Boot", 8);

        assertThat(curso.getNombre()).isEqualTo("Programación Avanzada");
        assertThat(curso.getDescripcion()).isEqualTo("Spring Boot");
        assertThat(curso.getCreditos()).isEqualTo(8);
    }

    @Test
    void actualizar_nombreVacio_lanzaDatoInvalidoException() {
        Curso curso = Curso.crear("Programación I", "Introducción a Java", 5);

        // Se pasa un nombre vacío ("") para que coincida con el nombre del test
        assertThatThrownBy(() -> curso.actualizar("", "Spring Boot", 5))
                .isInstanceOf(DatoInvalidoException.class);
    }
}
