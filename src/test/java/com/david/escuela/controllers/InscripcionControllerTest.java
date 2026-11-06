package com.david.escuela.controllers;

import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.dto.inscripciones.InscripcionResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.inscripciones.InscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InscripcionController.class)
public class InscripcionControllerTest {

    private static final String RUTA = "/api/inscripciones";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InscripcionService service;

    @Test
    void registrar_datosValidos_registraInscripcion() throws Exception {
        // Arrange
        InscripcionResponse response = new InscripcionResponse(1L, null, null, null, "05/11/2026");
        when(service.registrar(any(InscripcionRequest.class))).thenReturn(response);

        String json = "{\"idAlumno\":1,\"idGrupo\":1}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.calificacion").value(nullValue()));
    }

    @Test
    void registrar_alumnoYGrupoDuplicados_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(InscripcionRequest.class)))
                .thenThrow(new ConflictoException("La inscripción ya existe"));

        String json = "{\"idAlumno\":1,\"idGrupo\":1}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_alumnoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        when(service.registrar(any(InscripcionRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado"));

        String json = "{\"idAlumno\":999999,\"idGrupo\":1}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_inscripcionExistente_retornaInscripcion() throws Exception {
        // Arrange
        Long id = 1L;
        InscripcionResponse response = new InscripcionResponse(id, null, null, null, "05/11/2026");
        when(service.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.calificacion").value(nullValue()));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Inscripción no encontrada"));

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conInscripciones_retornaLista() throws Exception {
        // Arrange
        List<InscripcionResponse> lista = List.of(new InscripcionResponse(1L, null, null, null, "05/11/2026"));
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
    void actualizar_otroGrupoValido_actualizaInscripcion() throws Exception {
        // Arrange
        Long id = 1L;
        InscripcionResponse response = new InscripcionResponse(id, null, null, null, "05/11/2026");
        when(service.actualizar(any(InscripcionRequest.class), eq(id))).thenReturn(response);

        String json = "{\"idAlumno\":1,\"idGrupo\":2}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_combinacionYaExistente_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = 1L;
        when(service.actualizar(any(InscripcionRequest.class), eq(id)))
                .thenThrow(new ConflictoException("Combinación ya existente"));

        String json = "{\"idAlumno\":1,\"idGrupo\":1}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinCalificacion_eliminaInscripcion() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).eliminar(id);
    }

    @Test
    void eliminar_conCalificacion_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = 1L;
        doThrow(new ConflictoException("Entidad relacionada con calificación")).when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }
}