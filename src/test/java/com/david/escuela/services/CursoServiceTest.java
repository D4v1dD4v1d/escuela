package com.david.escuela.services;

import com.david.escuela.dto.cursos.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.CursoMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.cursos.CursoServiceImpl;
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
class CursoServiceTest {
    @Mock CursoRepository cursoRepository;
    @Mock GrupoReposiroty grupoRepository;
    @Mock CursoMapper cursoMapper;
    @InjectMocks CursoServiceImpl service;

    @Test
    void registrar_datosValidos_registraCurso() {
        // Arrange
        Curso entidad = curso();
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        when(cursoMapper.requestAEntidad(request)).thenReturn(entidad);
        CursoResponse esperado = respuesta();
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        CursoResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(cursoRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_nombreDuplicado_lanzaConflictoException() {
        // Arrange
        Curso entidad = curso();
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        
        when(cursoRepository.existsByNombreIgnoreCase("Matematicas I")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_cursoExistente_retornaCurso() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CursoResponse esperado = respuesta();
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        CursoResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conCursos_retornaLista() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findAll()).thenReturn(List.of(entidad));
        CursoResponse esperado = respuesta();
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<CursoResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(cursoRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }

    @Test
    void actualizar_mismoNombrePropio_noLanzaConflicto() {
        // Arrange
        Curso entidad = curso();
        CursoRequest request = new CursoRequest("Matematicas I",null,8);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CursoResponse esperado = respuesta();
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        CursoResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(cursoRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_nombreDeOtroCurso_lanzaConflictoException() {
        // Arrange
        Curso entidad = curso();
        CursoRequest request = new CursoRequest("Matematicas I",null,8);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        
        when(cursoRepository.existsByNombreIgnoreCaseAndIdNot("Matematicas I",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaCurso() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(cursoRepository).delete(entidad);
        verify(cursoRepository).flush();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.existsByCursoId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(cursoRepository,never()).delete(any());
    }

    private CursoResponse respuesta() {
        return new CursoResponse(1L,"Matematicas I","Fundamentos",6);
    }

    private Curso curso() {
        return Curso.builder().id(1L).nombre("Matematicas I").descripcion("Fundamentos").creditos(6).build();
    }
}
