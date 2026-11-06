package com.david.escuela.services;

import com.david.escuela.dto.alumnos.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.AlumnoMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.alumnos.AlumnoServiceImpl;
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
class AlumnoServiceTest {
    @Mock AlumnoRepository alumnoRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock AlumnoMapper alumnoMapper;
    @InjectMocks AlumnoServiceImpl service;

    @Test
    void registrar_datosValidos_generaMatriculaYEmail() {
        // Arrange
        Alumno entidad = alumno();
        AlumnoRequest request = new AlumnoRequest("Juan", "Perez", "Lopez");
        when(alumnoRepository.generarEmail(anyString(),anyString(),anyString())).thenReturn("juan@escuela.com");
        when(alumnoRepository.generarMatricula(anyString(),anyString(),anyString())).thenReturn("PELOJU2601");
        when(alumnoMapper.requestAEntidad(request,"juan@escuela.com","PELOJU2601")).thenReturn(entidad);
        AlumnoResponse esperado = respuesta();
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AlumnoResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(alumnoRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        AlumnoRequest request = new AlumnoRequest(" ","Perez","Lopez");
        when(alumnoRepository.generarEmail(anyString(),anyString(),anyString())).thenReturn("juan@escuela.com");
        when(alumnoRepository.generarMatricula(anyString(),anyString(),anyString())).thenReturn("PELOJU2601");
        when(alumnoMapper.requestAEntidad(request,"juan@escuela.com","PELOJU2601")).thenAnswer(invocacion -> Alumno.crear(request.nombre(),request.apellidoPaterno(),request.apellidoMaterno()));

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(alumnoRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_alumnoExistente_retornaAlumnoConPromedio() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AlumnoResponse esperado = respuesta();
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AlumnoResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_sinCalificaciones_retornaPromedioCero() {
        // Arrange
        Alumno entidad = alumno();
        
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(alumnoMapper.entidadAResponse(entidad)).thenAnswer(invocacion -> new AlumnoMapper().entidadAResponse(entidad));

        // Act
        AlumnoResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado.promedio()).isEqualByComparingTo("0");
    }

    @Test
    void obtenerPorId_conCalificacionesNulas_ignoraNulosEnPromedio() {
        // Arrange
        Alumno entidad = alumno();
        entidad.getInscripciones().add(Inscripcion.builder().alumno(entidad).grupo(grupo()).calificacion(Calificacion.crear(new BigDecimal("8"))).build());
        entidad.getInscripciones().add(Inscripcion.builder().alumno(entidad).grupo(grupo()).build());
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(alumnoMapper.entidadAResponse(entidad)).thenAnswer(invocacion -> new AlumnoMapper().entidadAResponse(entidad));

        // Act
        AlumnoResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado.promedio()).isEqualByComparingTo("8");
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(alumnoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conAlumnos_retornaLista() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findAll()).thenReturn(List.of(entidad));
        AlumnoResponse esperado = respuesta();
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<AlumnoResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(alumnoRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }


    @Test
    void actualizar_sinCambiosEnDatos_noRegeneraNiGuarda() {
        // Arrange
        Alumno entidad = alumno();
        AlumnoRequest request = new AlumnoRequest("Juan", "Perez", "Lopez");
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AlumnoResponse esperado = respuesta();
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        AlumnoResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(alumnoRepository,never()).generarEmail(anyString(),anyString(),anyString());
        verify(alumnoRepository,never()).generarMatricula(anyString(),anyString(),anyString());
        verify(alumnoRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        AlumnoRequest request = new AlumnoRequest("Pedro", "Perez", "Lopez");
        when(alumnoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.actualizar(request,999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
        verify(alumnoRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinInscripciones_eliminaAlumno() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(alumnoRepository).delete(entidad);
        verify(alumnoRepository).flush();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(inscripcionRepository.existsByAlumnoId(1L)).thenReturn(true);

        // Act
        EntidadRelacionada error = assertThrows(EntidadRelacionada.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(alumnoRepository,never()).delete(any());
    }

    private AlumnoResponse respuesta() {
        return new AlumnoResponse(1L,"Juan Perez Lopez","juan@escuela.com","PELOJU2601","01/01/2026",List.of(),BigDecimal.ZERO);
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
