package com.david.escuela.controllers;

import com.david.escuela.dto.grupos.GrupoRequest;
import com.david.escuela.dto.grupos.GrupoResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.grupos.GrupoService;
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

@WebMvcTest(GrupoController.class)
public class GrupoControllerTest {

    private static final String RUTA = "/api/grupos";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GrupoService service;

    @Test
    void registrar_datosValidos_registraGrupo() throws Exception {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L, 1L, 1L, "2026-02");
        GrupoResponse response = new GrupoResponse(1L, null, null, null, List.of(), "2026-02");
        when(service.registrar(any(GrupoRequest.class))).thenReturn(response);

        String json = "{\"idCurso\":1,\"idMaestro\":1,\"idAula\":1,\"periodo\":\"2026-02\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.periodo").value("2026-02"));
    }

    @Test
    void registrar_combinacionDuplicada_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(GrupoRequest.class)))
                .thenThrow(new ConflictoException("El grupo ya existe"));

        String json = "{\"idCurso\":1,\"idMaestro\":1,\"idAula\":1,\"periodo\":\"2026-01\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_cursoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        when(service.registrar(any(GrupoRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Curso no encontrado"));

        String json = "{\"idCurso\":999999,\"idMaestro\":1,\"idAula\":1,\"periodo\":\"2026-02\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_grupoExistente_retornaGrupoConHorarios() throws Exception {
        // Arrange
        Long id = 1L;
        GrupoResponse response = new GrupoResponse(id, null, null, null, List.of("Lunes 08:00 - 10:00"), "2026-01");
        when(service.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.horarios[0]").value("Lunes 08:00 - 10:00"));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado"));

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conGrupos_retornaLista() throws Exception {
        // Arrange
        List<GrupoResponse> lista = List.of(new GrupoResponse(1L, null, null, null, List.of("Lunes 08:00 - 10:00"), "2026-01"));
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
    void actualizar_datosValidos_actualizaGrupo() throws Exception {
        // Arrange
        Long id = 1L;
        GrupoResponse response = new GrupoResponse(id, null, null, null, List.of("Lunes 08:00 - 10:00"), "2026-01");
        when(service.actualizar(any(GrupoRequest.class), eq(id))).thenReturn(response);

        String json = "{\"idCurso\":1,\"idMaestro\":1,\"idAula\":1,\"periodo\":\"2026-01\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_combinacionDeOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = 2L;
        when(service.actualizar(any(GrupoRequest.class), eq(id)))
                .thenThrow(new ConflictoException("Conflicto de combinación"));

        String json = "{\"idCurso\":1,\"idMaestro\":1,\"idAula\":1,\"periodo\":\"2026-01\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinInscripcionesNiHorarios_eliminaGrupo() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).eliminar(id);
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = 1L;
        doThrow(new ConflictoException("Entidad relacionada")).when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }
}