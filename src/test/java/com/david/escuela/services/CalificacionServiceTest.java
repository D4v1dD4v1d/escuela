package com.david.escuela.services;

import com.david.escuela.dto.calificaciones.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.CalificacionMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.calificaciones.CalificacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {
    @Mock CalificacionRepository calificacionRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock CalificacionMapper calificacionMapper;
    @InjectMocks CalificacionServiceImpl service;

    @Test
    void registrar_datosValidos_registraCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        CalificacionRequest request = new CalificacionRequest(15L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(anyLong())).thenAnswer(invocacion -> Optional.ofNullable(Map.of(15L,entidad.getInscripcion()).get(invocacion.getArgument(0))));
        when(calificacionMapper.requestAEntidad(request,entidad.getInscripcion())).thenReturn(entidad);
        CalificacionResponse esperado = respuesta();
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        CalificacionResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(calificacionRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_inscripcionYaCalificada_lanzaConflictoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(15L,new BigDecimal("8.5"));
        
        when(calificacionRepository.existsByInscripcionId(15L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_inscripcionInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(15L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_calificacionExistente_retornaCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(entidad));
        CalificacionResponse esperado = respuesta();
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        CalificacionResponse resultado = service.obtenerPorId(2L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(calificacionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conCalificaciones_retornaLista() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findAll()).thenReturn(List.of(entidad));
        CalificacionResponse esperado = respuesta();
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<CalificacionResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(calificacionRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }


    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(15L,new BigDecimal("9"));
        when(calificacionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.actualizar(request,999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_calificacionExistente_eliminaCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(2L);

        // Assert
        verify(calificacionRepository).delete(entidad);
        verify(calificacionRepository).flush();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(calificacionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.eliminar(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
        verify(calificacionRepository,never()).delete(any());
    }

    private CalificacionResponse respuesta() {
        return new CalificacionResponse(2L,null,new BigDecimal("8.5"),"01/01/2026");
    }

    private Calificacion calificacion() {
        return Calificacion.builder().id(2L).inscripcion(inscripcion()).calificacion(new BigDecimal("8.5")).fechaRegistro(LocalDate.now()).build();
    }

    private Inscripcion inscripcion() {
        return Inscripcion.builder().id(15L).alumno(alumno()).grupo(grupo()).fechaInscripcion(LocalDate.now()).build();
    }

    private Alumno alumno() {
        Alumno alumno = Alumno.builder().id(1L).nombre("Juan").apellidoPaterno("Perez").apellidoMaterno("Lopez").build();
        alumno.asignarDatosAcademicos("juan@escuela.com", "PELOJU2601");
        return alumno;
    }

    private Grupo grupo() {
        return Grupo.builder().id(1L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-01").build();
    }

    private Curso curso() {
        return Curso.builder().id(1L).nombre("Matematicas I").descripcion("Fundamentos").creditos(6).build();
    }

    private Maestro maestro() {
        return Maestro.builder().id(1L).nombre("Laura").apellidoPaterno("Martinez").apellidoMaterno("Lopez").email("laura@escuela.com").telefono("5512345678").build();
    }

    private Aula aula() {
        return Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
    }
}
