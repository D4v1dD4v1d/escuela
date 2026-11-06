package com.david.escuela.controllers;

import com.david.escuela.dto.cursos.CursoRequest;
import com.david.escuela.dto.cursos.CursoResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.cursos.CursoService;
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

@WebMvcTest(CursoController.class)
public class CursoControllerTest {

    private static final String RUTA = "/api/cursos";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CursoService service;

    @Test
    void registrar_datosValidos_registraCurso() throws Exception {
        // Arrange
        CursoRequest request = new CursoRequest("Bases de Datos", "SQL", 8);
        CursoResponse response = new CursoResponse(1L, "Bases de Datos", "SQL", 8);
        when(service.registrar(any(CursoRequest.class))).thenReturn(response);

        String json = "{\"nombre\":\"Bases de Datos\",\"descripcion\":\"SQL\",\"creditos\":8}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Bases de Datos"));
    }

    @Test
    void registrar_nombreDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(CursoRequest.class)))
                .thenThrow(new ConflictoException("El curso ya existe"));

        String json = "{\"nombre\":\"Matematicas I\",\"creditos\":6}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_cursoExistente_retornaCurso() throws Exception {
        // Arrange
        Long id = 1L;
        CursoResponse response = new CursoResponse(id, "Matematicas I", "Fundamentos", 6);
        when(service.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.creditos").value(6));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Curso no encontrado"));

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conCursos_retornaLista() throws Exception {
        // Arrange
        List<CursoResponse> lista = List.of(new CursoResponse(1L, "Matematicas I", "Fundamentos", 6));
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
    void actualizar_mismoNombrePropio_noLanzaConflicto() throws Exception {
        // Arrange
        Long id = 1L;
        CursoResponse response = new CursoResponse(id, "Matematicas I", "Fundamentos", 8);
        when(service.actualizar(any(CursoRequest.class), eq(id))).thenReturn(response);

        String json = "{\"nombre\":\"Matematicas I\",\"creditos\":8}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_nombreDeOtroCurso_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = 2L;
        when(service.actualizar(any(CursoRequest.class), eq(id)))
                .thenThrow(new ConflictoException("Conflicto de nombre"));

        String json = "{\"nombre\":\"Matematicas I\",\"creditos\":6}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinGrupos_eliminaCurso() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).eliminar(id);
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() throws Exception {
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