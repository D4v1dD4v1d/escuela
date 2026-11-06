package com.david.escuela.controllers;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.aulas.AulaResponse;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.aulas.AulaService;
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

@WebMvcTest(AulaController.class)
public class AulaControllerTest {

    private static final String RUTA = "/api/aulas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AulaService aulaService;

    @Test
    void registrar_datosValidos_registraAula() throws Exception {
        // Arrange
        AulaRequest request = new AulaRequest("Aula 202", 40);
        AulaResponse response = new AulaResponse(1L, "Aula 202", 40);

        when(aulaService.registrar(any(AulaRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.capacidad").value(40));
    }

    @Test
    void obtenerPorId_aulaExistente_retornaAula() throws Exception {
        // Arrange
        Long id = 1L;
        AulaResponse response = new AulaResponse(id, "Aula 101", 30);

        when(aulaService.obtenerPorId(id)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.capacidad").value(30));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        when(aulaService.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException("Aula no encontrada con id: " + id));

        // Act & Assert
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Aula no encontrada con id: " + id));
    }

    @Test
    void listar_conAulas_retornaLista() throws Exception {
        // Arrange
        List<AulaResponse> lista = List.of(new AulaResponse(1L, "Aula 101", 30));
        when(aulaService.lista()).thenReturn(lista);

        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Aula 101"));
    }

    @Test
    void actualizar_datosValidos_retornaAulaActualizada() throws Exception {
        // Arrange
        Long id = 1L;
        AulaRequest request = new AulaRequest("Aula 101", 40);
        AulaResponse response = new AulaResponse(id, "Aula 101", 40);

        when(aulaService.actualizar(request, id)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidad").value(40));
    }

    @Test
    void eliminar_sinGrupos_eliminaAula() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(aulaService).eliminar(id);

        // Act & Assert
        mockMvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(aulaService).eliminar(id);
    }
}