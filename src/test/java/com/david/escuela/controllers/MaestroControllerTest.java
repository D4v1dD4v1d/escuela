package com.david.escuela.controllers;

import com.david.escuela.dto.maestros.MaestroRequest;
import com.david.escuela.dto.maestros.MaestroResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.maestros.MaestroService;
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

@WebMvcTest(MaestroController.class)
public class MaestroControllerTest {

    private static final String RUTA = "/api/maestros";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaestroService service;



    @Test
    void registrar_emailDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(MaestroRequest.class)))
                .thenThrow(new ConflictoException("El email ya está registrado"));

        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"laura@escuela.com\",\"telefono\":\"5598765432\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_telefonoDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        when(service.registrar(any(MaestroRequest.class)))
                .thenThrow(new ConflictoException("El teléfono ya está registrado"));

        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"ana@escuela.com\",\"telefono\":\"5512345678\"}";

        // Act & Assert
        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_maestroExistente_retornaMaestroConCursos() throws Exception {
        // Arrange
        Long id = 1L;
        MaestroResponse response = new MaestroResponse(id, "Laura", "test@test.com", "5212345698", List.of());
        when(service.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Maestro no encontrado"));

        // Act & Assert
        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conMaestros_retornaLista() throws Exception {
        // Arrange
        List<MaestroResponse> lista = List.of(new MaestroResponse(Long.valueOf(1), "Laura", "test@test.com", "5212345698", List.of()));
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
    void actualizar_mismoEmailYTelefonoPropios_noLanzaConflicto() throws Exception {
        // Arrange
        Long id = 1L;
        MaestroResponse response = new MaestroResponse(id, "Laura", "test@test.com", "5212345698", List.of());
        when(service.actualizar(any(MaestroRequest.class), eq(id))).thenReturn(response);

        String json = "{\"nombre\":\"Laura\",\"apellidoPaterno\":\"Martinez\",\"apellidoMaterno\":\"Lopez\",\"email\":\"laura@escuela.com\",\"telefono\":\"5512345678\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_emailDeOtroMaestro_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = 2L;
        when(service.actualizar(any(MaestroRequest.class), eq(id)))
                .thenThrow(new ConflictoException("Conflicto de email o teléfono"));

        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"laura@escuela.com\",\"telefono\":\"5598765432\"}";

        // Act & Assert
        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinGrupos_eliminaMaestro() throws Exception {
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
        doThrow(new ConflictoException("Entidad relacionada con grupos")).when(service).eliminar(id);

        // Act & Assert
        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }
}