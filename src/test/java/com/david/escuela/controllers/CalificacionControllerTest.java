
package com.david.escuela.controllers;

import com.david.escuela.dto.calificaciones.CalificacionRequest;
import com.david.escuela.dto.calificaciones.CalificacionResponse;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.calificaciones.CalificacionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalificacionController.class)
class CalificacionControllerTest {

    private static final String RUTA = "/api/calificaciones";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalificacionService service;

    // REGISTRAR

    @Test
    void registrar_datosValidos_retorna201() throws Exception {

        CalificacionRequest request = new CalificacionRequest(
                1L,
                new BigDecimal("8.5")
        );

        CalificacionResponse response = new CalificacionResponse(
                1L,
                null,
                new BigDecimal("8.5"),
                "05/11/2026"
        );

        when(service.registrar(any(CalificacionRequest.class)))
                .thenReturn(response);

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.calificacion").value(8.5));

        verify(service).registrar(any(CalificacionRequest.class));
    }

    @Test
    void registrar_inscripcionYaCalificada_retorna409() throws Exception {

        CalificacionRequest request = new CalificacionRequest(
                1L,
                new BigDecimal("8.5")
        );

        when(service.registrar(any(CalificacionRequest.class)))
                .thenThrow(new ConflictoException(
                        "La inscripción ya cuenta con una calificación."
                ));

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verify(service).registrar(any(CalificacionRequest.class));
    }

    @Test
    void registrar_inscripcionInexistente_retorna404() throws Exception {

        CalificacionRequest request = new CalificacionRequest(
                999999L,
                new BigDecimal("8.5")
        );

        when(service.registrar(any(CalificacionRequest.class)))
                .thenThrow(new RecursoNoEncontradoException(
                        "Inscripción no encontrada"
                ));

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verify(service).registrar(any(CalificacionRequest.class));
    }

    @Test
    void registrar_calificacionNula_retorna400() throws Exception {

        String json = """
                {
                    "idInscripcion": 1,
                    "calificacion": null
                }
                """;

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).registrar(any(CalificacionRequest.class));
    }

    @Test
    void registrar_idInscripcionNulo_retorna400() throws Exception {

        String json = """
                {
                    "idInscripcion": null,
                    "calificacion": 8.5
                }
                """;

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).registrar(any(CalificacionRequest.class));
    }

    @Test
    void registrar_calificacionNegativa_retorna400() throws Exception {

        String json = """
                {
                    "idInscripcion": 1,
                    "calificacion": -5
                }
                """;

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).registrar(any(CalificacionRequest.class));
    }

    // OBTENER POR ID

    @Test
    void obtenerPorId_calificacionExistente_retornaCalificacion() throws Exception {

        Long id = 1L;

        CalificacionResponse response = new CalificacionResponse(
                id,
                null,
                new BigDecimal("8.5"),
                "05/11/2026"
        );

        when(service.obtenerPorId(id)).thenReturn(response);

        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.calificacion").value(8.5));

        verify(service).obtenerPorId(id);
    }

    @Test
    void obtenerPorId_idInexistente_retorna404() throws Exception {

        Long id = 999999L;

        when(service.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException(
                        "Calificación no encontrada"
                ));

        mvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verify(service).obtenerPorId(id);
    }

    // LISTAR

    @Test
    void listar_conCalificaciones_retornaLista() throws Exception {

        List<CalificacionResponse> lista = List.of(
                new CalificacionResponse(
                        1L,
                        null,
                        new BigDecimal("8.5"),
                        "05/11/2026"
                ),
                new CalificacionResponse(
                        2L,
                        null,
                        new BigDecimal("9.0"),
                        "05/11/2026"
                )
        );

        when(service.lista()).thenReturn(lista);

        mvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].calificacion").value(9.0));

        verify(service).lista();
    }

    @Test
    void listar_sinCalificaciones_retornaListaVacia() throws Exception {

        when(service.lista()).thenReturn(List.of());

        mvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).lista();
    }

    @Test
    void listar_errorDeRepositorio_retorna500() throws Exception {

        doThrow(new IllegalStateException("Fallo simulado"))
                .when(service).lista();

        mvc.perform(get(RUTA))
                .andExpect(status().isInternalServerError());

        verify(service).lista();
    }

    // ACTUALIZAR

    @Test
    void actualizar_valorValido_retornaCalificacionActualizada() throws Exception {

        Long id = 1L;

        CalificacionResponse response = new CalificacionResponse(
                id,
                null,
                new BigDecimal("9"),
                "05/11/2026"
        );

        String json = """
                {
                    "idInscripcion": 2,
                    "calificacion": 9
                }
                """;

        when(service.actualizar(any(CalificacionRequest.class), eq(id)))
                .thenReturn(response);

        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.calificacion").value(9));

        verify(service).actualizar(any(CalificacionRequest.class), eq(id));
    }

    @Test
    void actualizar_idInexistente_retorna404() throws Exception {

        Long id = 999999L;

        String json = """
                {
                    "idInscripcion": 1,
                    "calificacion": 8.5
                }
                """;

        when(service.actualizar(any(CalificacionRequest.class), eq(id)))
                .thenThrow(new RecursoNoEncontradoException(
                        "Calificación no encontrada"
                ));

        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verify(service).actualizar(any(CalificacionRequest.class), eq(id));
    }

    @Test
    void actualizar_calificacionNula_retorna400() throws Exception {

        Long id = 1L;

        String json = """
                {
                    "idInscripcion": 1,
                    "calificacion": null
                }
                """;

        mvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never())
                .actualizar(any(CalificacionRequest.class), eq(id));
    }

    // ELIMINAR

    @Test
    void eliminar_calificacionExistente_retorna204() throws Exception {

        Long id = 1L;

        doNothing().when(service).eliminar(id);

        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).eliminar(id);
    }

    @Test
    void eliminar_idInexistente_retorna404() throws Exception {

        Long id = 999999L;

        doThrow(new RecursoNoEncontradoException(
                "Calificación no encontrada"
        )).when(service).eliminar(id);

        mvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verify(service).eliminar(id);
    }
}
