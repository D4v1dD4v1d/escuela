package com.david.escuela.entities;
import com.david.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class AulaTest {

    //crear
    @Test
    void crear_datosValidos_retornaAula() {
        Aula aula = Aula.crear("Laboratorio de Computo 1", 30);
        assertThat(aula).isNotNull();
        assertThat(aula.getNombre()).isEqualTo("Laboratorio de Computo 1");
        assertThat(aula.getCapacidad()).isEqualTo(30);
    }

    @Test
    void crear_capacidadNoPositiva_lanzaDatoInvalidoException() {
        assertThatThrownBy(() -> Aula.crear("Laboratorio de Computo 1", 0))
                .isInstanceOf(DatoInvalidoException.class);
    }
    //actualizar
    @Test
    void actualizar_datosValidos_actualizaCampos() {
        Aula aula = Aula.crear("Laboratorio de Computo 1", 30);
        aula.actualizar("Aula Magna Principal", 25);
        assertThat(aula.getNombre()).isEqualTo("Aula Magna Principal");
        assertThat(aula.getCapacidad()).isEqualTo(25);
    }

    @Test
    void actualizar_nombreVacio_lanzaDatoInvalidoException() {
        Aula aula = Aula.crear("Laboratorio de Computo 1", 30);
        assertThatThrownBy(() -> aula.actualizar("", 25))
                .isInstanceOf(DatoInvalidoException.class);
    }
}
