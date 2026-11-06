package com.david.escuela.services;

import com.david.escuela.dto.grupos.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.GrupoMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.grupos.GrupoServiceImpl;
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
class GrupoServiceTest {
    @Mock GrupoReposiroty grupoRepository;
    @Mock CursoRepository cursoRepository;
    @Mock MaestroRepository maestroRepository;
    @Mock AulaRepository aulaRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock HorarioRepository horarioRepository;
    @Mock GrupoMapper grupoMapper;
    @InjectMocks GrupoServiceImpl service;

    @Test
    void registrar_datosValidos_registraGrupo() {
        // Arrange
        Grupo entidad = grupo();
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad.getCurso()));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad.getMaestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad.getAula()));
        when(grupoMapper.requestAEntidad(request,entidad.getCurso(),entidad.getMaestro(),entidad.getAula())).thenReturn(entidad);
        GrupoResponse esperado = respuesta();
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        GrupoResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(grupoRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_periodoFormatoValido_registraGrupo() {
        // Arrange
        Grupo entidad = grupo();
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad.getCurso()));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad.getMaestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad.getAula()));
        when(grupoMapper.requestAEntidad(request,entidad.getCurso(),entidad.getMaestro(),entidad.getAula())).thenReturn(entidad);
        GrupoResponse esperado = respuesta();
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        service.registrar(request);

        // Assert
        verify(grupoRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_combinacionDuplicada_lanzaConflictoException() {
        // Arrange
        Grupo entidad = grupo();
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad.getCurso()));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad.getMaestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad.getAula()));
        when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(1L,1L,1L,"2026-01")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_cursoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_grupoExistente_retornaGrupoConHorarios() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        GrupoResponse esperado = respuesta();
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        GrupoResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(grupoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conGrupos_retornaLista() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findAll()).thenReturn(List.of(entidad));
        GrupoResponse esperado = respuesta();
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<GrupoResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(grupoRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }

    @Test
    void actualizar_datosValidos_actualizaGrupo() {
        // Arrange
        Grupo entidad = grupo();
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-02");
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad.getCurso()));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad.getMaestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad.getAula()));
        GrupoResponse esperado = respuesta();
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        GrupoResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(grupoRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_combinacionDeOtroGrupo_lanzaConflictoException() {
        // Arrange
        Grupo entidad = grupo();
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-02");
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad.getCurso()));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad.getMaestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad.getAula()));
        when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(1L,1L,1L,"2026-02",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinInscripcionesNiHorarios_eliminaGrupo() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(grupoRepository).delete(entidad);
        verify(grupoRepository).flush();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(inscripcionRepository.existsByGrupoId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).delete(any());
    }

    @Test
    void eliminar_conHorarios_lanzaEntidadRelacionadaException() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(horarioRepository.existsByGrupoId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).delete(any());
    }

    private GrupoResponse respuesta() {
        return new GrupoResponse(1L,null,null,null,List.of("Lunes 08:00 - 10:00"),"2026-01");
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
