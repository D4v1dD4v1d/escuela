package com.david.escuela.services;

import com.david.escuela.dto.horarios.*;
import com.david.escuela.entities.*;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.*;
import com.david.escuela.mappers.HorarioMapper;
import com.david.escuela.repositories.*;
import com.david.escuela.services.horarios.HorarioServiceImpl;
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
class HorarioServiceTest {
    @Mock HorarioRepository horarioRepository;
    @Mock GrupoReposiroty grupoRepository;
    @Mock HorarioMapper horarioMapper;
    @InjectMocks HorarioServiceImpl service;

    @Test
    void registrar_datosValidos_registraHorario() {
        // Arrange
        Horario entidad = horario();
        HorarioRequest request = new HorarioRequest(1L,"Lunes","08:00","10:00");
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(horarioMapper.requestAEntidad(request,entidad.getGrupo())).thenReturn(entidad);
        HorarioResponse esperado = respuesta();
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        HorarioResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(horarioRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_mismoGrupoDatosIgualesExceptoPeriodo_registraExitosamente() {
        // Arrange
        Horario entidad = horario();
        Grupo siguiente = Grupo.builder().id(2L).curso(entidad.getGrupo().getCurso()).maestro(entidad.getGrupo().getMaestro()).aula(entidad.getGrupo().getAula()).periodo("2026-02").build();
        HorarioRequest request = new HorarioRequest(2L,"Lunes","08:00","10:00");
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(siguiente));
        when(horarioMapper.requestAEntidad(request,siguiente)).thenAnswer(invocacion -> {
            Horario nuevo = Horario.crear(request.dia(),request.horaInicio(),request.horaFin());
            nuevo.asignarGrupo(siguiente);
            return nuevo;
        });
        when(horarioMapper.entidadAResponse(any(Horario.class))).thenReturn(respuesta());

        // Act
        HorarioResponse resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(horarioRepository).buscarHorariosFormateados("08:00","10:00","Lunes",2L,1L,"2026-02");
        verify(horarioRepository).saveAndFlush(any(Horario.class));
    }

    @Test
    void registrar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","08:00");
        

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_horaInicioIgualAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","10:00");
        

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() {
        // Arrange
        Horario entidad = horario();
        HorarioRequest request = new HorarioRequest(1L,"Lunes","08:00","10:00");
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(horarioRepository.buscarHorariosFormateados("08:00","10:00","Lunes",1L,1L,"2026-01")).thenReturn(List.of("CONFLICTO: El aula esta ocupada"));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_traslapeEnElMismoGrupo_lanzaConflictoException() {
        // Arrange
        Horario entidad = horario();
        HorarioRequest request = new HorarioRequest(1L,"Lunes","08:00","10:00");
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(horarioRepository.buscarHorariosFormateados("08:00","10:00","Lunes",1L,1L,"2026-01")).thenReturn(List.of("CONFLICTO: El grupo esta ocupado"));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_grupoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,"Lunes","08:00","10:00");
        when(grupoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_horarioExistente_retornaHorario() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioResponse esperado = respuesta();
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        HorarioResponse resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(horarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
    }

    @Test
    void listar_conHorarios_retornaLista() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findAll()).thenReturn(List.of(entidad));
        HorarioResponse esperado = respuesta();
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        List<HorarioResponse> resultado = service.lista();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("fallo simulado");
        when(horarioRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,() -> service.lista());

        // Assert
        assertThat(error).isSameAs(causa);
    }

    @Test
    void actualizar_datosValidos_actualizaHorario() {
        // Arrange
        Horario entidad = horario();
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","12:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        HorarioResponse esperado = respuesta();
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        HorarioResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(entidad.getHoraInicio()).isEqualTo("10:00");
        verify(horarioRepository).buscarHorariosFormateados("10:00","12:00",1L,1L,1L,"2026-01","Lunes");
        verify(horarioRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_mismoGrupoDatosIgualesExceptoPeriodo_actualizaExitosamente() {
        // Arrange
        Horario entidad = horario();
        Grupo siguiente = Grupo.builder().id(2L).curso(entidad.getGrupo().getCurso()).maestro(entidad.getGrupo().getMaestro()).aula(entidad.getGrupo().getAula()).periodo("2026-02").build();
        HorarioRequest request = new HorarioRequest(2L,"Lunes","08:00","10:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(siguiente));
        when(horarioMapper.entidadAResponse(any(Horario.class))).thenReturn(respuesta());

        // Act
        HorarioResponse resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(horarioRepository).buscarHorariosFormateados("08:00","10:00",2L,1L,1L,"2026-02","Lunes");
        verify(horarioRepository).saveAndFlush(any(Horario.class));
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","08:00");
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() {
        // Arrange
        Horario entidad = horario();
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","12:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad.getGrupo()));
        when(horarioRepository.buscarHorariosFormateados("10:00","12:00",1L,1L,1L,"2026-01","Lunes")).thenReturn(List.of("CONFLICTO: El aula esta ocupada"));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,"Lunes","10:00","12:00");
        when(horarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.actualizar(request,999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_horarioExistente_eliminaHorario() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(horarioRepository).delete(entidad);
        verify(horarioRepository).flush();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        when(horarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.eliminar(999L));

        // Assert
        assertThat(error.getMessage()).contains("999");
        verify(horarioRepository,never()).delete(any());
    }

    private HorarioResponse respuesta() {
        return new HorarioResponse(1L,null,"Lunes 08:00 10:00");
    }

    private Horario horario() {
        return Horario.builder().id(1L).grupo(grupo()).diaSemana(DiaSemana.LUNES).horaInicio("08:00").horaFin("10:00").build();
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
