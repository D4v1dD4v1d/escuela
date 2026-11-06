package com.david.escuela.controllers;

import com.david.escuela.dto.horarios.HorarioRequest;
import com.david.escuela.dto.horarios.HorarioResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.horarios.HorarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioController.class)
public class HorarioControllerTest {

    private static final String RUTA = "/api/horarios";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HorarioService service;

    @Test
    void registrar_datosValidos_registraHorario() throws Exception {
        // Arrange
        HorarioResponse response = new HorarioResponse(1L, null, "Lunes 08:00 10:00");
        when(service.registrar(any(HorarioRequest.class))).thenReturn(response);

        String json = "{\"idGrupo\":1,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.horario").value("Lunes 08:00 10:00"));
    }

    @Test
    void registrar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(HorarioRequest.class)))
                .thenThrow(new ConflictoException("El aula ya está ocupada en ese horario"));

        String json = "{\"idGrupo\":1,\"dia\":\"Lunes\",\"horaInicio\":\"09:00\",\"horaFin\":\"11:00\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_grupoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        when(service.registrar(any(HorarioRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado"));

        String json = "{\"idGrupo\":999999,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_horarioExistente_retornaHorario() throws Exception {
        // Arrange
        Long id = 1L;
        HorarioResponse response = new HorarioResponse(id, null, "Lunes 08:00 10:00");
        when(service.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.horario").value("Lunes 08:00 10:00"));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Horario no encontrado"));

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conHorarios_retornaLista() throws Exception {
        // Arrange
        List<HorarioResponse> lista = List.of(new HorarioResponse(1L, null, "Lunes 08:00 10:00"));
        when(service.lista()).thenReturn(lista);

        // Act & Assert
        mvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("fallo simulado")).when(service).lista();

        // Act & Assert
        mvc.perform(get(RUTA))
                .andExpect(status().is(500))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_datosValidos_actualizaHorario() throws Exception {
        // Arrange
        Long id = 1L;
        HorarioResponse response = new HorarioResponse(id, null, "Lunes 08:00 10:00");
        when(service.actualizar(any(HorarioRequest.class), eq(id))).thenReturn(response);

        String json = "{\"idGrupo\":1,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.actualizar(any(HorarioRequest.class), eq(id)))
                .thenThrow(new RecursoNoEncontradoException("Horario no encontrado"));

        String json = "{\"idGrupo\":1,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_horarioExistente_eliminaHorario() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).eliminar(id);
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        doThrow(new RecursoNoEncontradoException("Horario no encontrado")).when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }
}