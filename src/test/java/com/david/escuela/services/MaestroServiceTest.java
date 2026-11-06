package com.david.escuela.services;

import com.david.escuela.dto.maestros.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.MaestroMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.maestros.MaestroServiceImpl;
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
class MaestroServiceTest {
    @Mock MaestroRepository maestroRepository;
    @Mock GrupoReposiroty grupoRepository;
    @Mock MaestroMapper maestroMapper;
    @InjectMocks MaestroServiceImpl service;

    @Test
    void registrar_datosValidos_registraMaestro() {
        // Arrange
        Maestro entidad = maestro();
        MaestroRequest request = new MaestroRequest("Laura","Martinez","Lopez","laura@escuela.com","5512345678");
        when(maestroMapper.requestAEntidad(request)).thenReturn(entidad);
        MaestroResponse esperado = respuesta();
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        MaestroResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(maestroRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_emailDuplicado_lanzaConflictoException() {
        // Arrange
        Maestro entidad = maestro();
        MaestroRequest request = new MaestroRequest("Laura","Martinez","Lopez","laura@escuela.com","5512345678");
        
        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_telefonoDuplicado_lanzaConflictoException() {
        // Arrange
        MaestroRequest request = new MaestroRequest("Laura","Martinez","Lopez","laura@escuela.com","5512345678");
        when(maestroRepository.existsByTelefono("5512345678")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_maestroExistente_retornaMaestroConCursos() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        MaestroResponse esperado = respuesta();
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        MaestroResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(maestroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conMaestros_retornaLista() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findAll()).thenReturn(List.of(entidad));
        MaestroResponse esperado = respuesta();
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<MaestroResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(maestroRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }


    @Test
    void actualizar_emailDeOtroMaestro_lanzaConflictoException() {
        // Arrange
        Maestro entidad = maestro();
        MaestroRequest request = new MaestroRequest("Laura","Martinez","Lopez","laura@escuela.com","5512345678");
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        
        when(maestroRepository.existsByEmailIgnoreCaseAndIdNot("laura@escuela.com",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaMaestro() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(maestroRepository).delete(entidad);
        verify(maestroRepository).flush();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.existsByMaestroId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).delete(any());
    }

    private MaestroResponse respuesta() {
        return new MaestroResponse(1L,"Laura Martinez Lopez","laura@escuela.com","5512345678",List.of(new com.david.escuela.dto.datos.DatosCurso("Matematicas I",null,6)));
    }

    private Maestro maestro() {
        return Maestro.builder().id(1L).nombre("Laura").apellidoPaterno("Martinez").apellidoMaterno("Lopez").email("laura@escuela.com").telefono("5512345678").build();
    }
}
