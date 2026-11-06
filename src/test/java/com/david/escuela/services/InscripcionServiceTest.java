package com.david.escuela.services;

import com.david.escuela.dto.inscripciones.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.InscripcionMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.inscripciones.InscripcionServiceImpl;
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
class InscripcionServiceTest {
    @Mock InscripcionRepository inscripcionRepository;
    @Mock AlumnoRepository alumnoRepository;
    @Mock GrupoReposiroty grupoRepository;
    @Mock CalificacionRepository calificacionRepository;
    @Mock InscripcionMapper inscripcionMapper;
    @InjectMocks InscripcionServiceImpl service;

    @Test
    void registrar_datosValidos_registraInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad.getAlumno()));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(inscripcionMapper.requestAEntidad(request,entidad.getAlumno(),entidad.getGrupo())).thenReturn(entidad);
        InscripcionResponse esperado = respuesta();
        lenient().when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        InscripcionResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(inscripcionRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_alumnoYGrupoDuplicados_lanzaConflictoException() {
        // Arrange
        Inscripcion entidad = inscripcion();
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad.getAlumno()));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(inscripcionRepository.existsByGrupoIdAndAlumnoId(1L,1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_alumnoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_inscripcionExistente_retornaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(entidad));
        InscripcionResponse esperado = respuesta();
        lenient().when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        InscripcionResponse resultado = service.obtenerPorId(15L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(inscripcionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conInscripciones_retornaLista() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findAll()).thenReturn(List.of(entidad));
        InscripcionResponse esperado = respuesta();
        lenient().when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<InscripcionResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(inscripcionRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }

    @Test
    void actualizar_otroGrupoValido_actualizaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        Grupo nuevoGrupo = Grupo.builder().id(2L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-02").build();
        InscripcionRequest request = new InscripcionRequest(1L,2L);
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(entidad));
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad.getAlumno()));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(nuevoGrupo));
        InscripcionResponse esperado = respuesta();
        lenient().when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        InscripcionResponse resultado = service.actualizar(request,15L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(entidad.getGrupo()).isSameAs(nuevoGrupo);
        verify(inscripcionRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_combinacionYaExistente_lanzaConflictoException() {
        // Arrange
        Inscripcion entidad = inscripcion();
        InscripcionRequest request = new InscripcionRequest(1L,2L);
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(entidad));
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad.getAlumno()));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(inscripcionRepository.existsByGrupoIdAndAlumnoIdAndIdNot(2L,1L,15L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,15L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinCalificacion_eliminaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(15L);

        // Assert
        verify(inscripcionRepository).delete(entidad);
        verify(inscripcionRepository).flush();
    }

    @Test
    void eliminar_conCalificacion_lanzaEntidadRelacionadaException() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(entidad));
        when(calificacionRepository.existsByInscripcionId(15L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(15L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).delete(any());
    }

    private InscripcionResponse respuesta() {
        return new InscripcionResponse(15L,null,null,null,"01/01/2026");
    }

    private Maestro maestro() {
        return Maestro.builder().id(1L).nombre("Laura").apellidoPaterno("Martinez").apellidoMaterno("Lopez").email("laura@escuela.com").telefono("5512345678").build();
    }

    private Aula aula() {
        return Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
    }

    private Curso curso() {
        return Curso.builder().id(1L).nombre("Matematicas I").descripcion("Fundamentos").creditos(6).build();
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
}
