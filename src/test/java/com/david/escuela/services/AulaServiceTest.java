package com.david.escuela.services;

import com.david.escuela.dto.aulas.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.AulaMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.aulas.AulaServiceImpl;
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
class AulaServiceTest {
    @Mock AulaRepository aulaRepository;
    @Mock GrupoReposiroty grupoRepository;
    @Mock AulaMapper aulaMapper;
    @InjectMocks AulaServiceImpl service;

    @Test
    void registrar_datosValidos_registraAula() {
        // Arrange
        Aula entidad = aula();
        AulaRequest request = new AulaRequest("Aula 101",30);
        when(aulaMapper.requestAEntidad(request)).thenReturn(entidad);
        AulaResponse esperado = respuesta();
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AulaResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(aulaRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_nombreDuplicado_lanzaConflictoException() {
        // Arrange
        Aula entidad = aula();
        AulaRequest request = new AulaRequest("Aula 101",30);
        
        when(aulaRepository.existsByNombreIgnoreCase("Aula 101")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_aulaExistente_retornaAula() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AulaResponse esperado = respuesta();
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AulaResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(aulaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conAulas_retornaLista() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findAll()).thenReturn(List.of(entidad));
        AulaResponse esperado = respuesta();
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<AulaResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(aulaRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }

    @Test
    void actualizar_mismoNombrePropio_noLanzaConflicto() {
        // Arrange
        Aula entidad = aula();
        AulaRequest request = new AulaRequest("Aula 101",40);
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AulaResponse esperado = respuesta();
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AulaResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(aulaRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_nombreDeOtraAula_lanzaConflictoException() {
        // Arrange
        Aula entidad = aula();
        AulaRequest request = new AulaRequest("Aula 101",40);
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        
        when(aulaRepository.existsByNombreIgnoreCaseAndIdNot("Aula 101",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaAula() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(aulaRepository).delete(entidad);
        verify(aulaRepository).flush();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.existsByAulaId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).delete(any());
    }

    private AulaResponse respuesta() {
        return new AulaResponse(1L,"Aula 101",30);
    }

    private Aula aula() {
        return Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
    }
}
