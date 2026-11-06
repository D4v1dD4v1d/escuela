
package com.david.escuela.controllers;

import com.david.escuela.dto.alumnos.AlumnoRequest;
import com.david.escuela.dto.alumnos.AlumnoResponse;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.services.alumnos.AlumnoService;
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

@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

    private static final String RUTA = "/api/alumnos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlumnoService alumnoService;

    @Test
    void registrar_datosValidos_retorna201() throws Exception {
        AlumnoRequest request = new AlumnoRequest(
                "Maria",
                "Gomez",
                "Ramos"
        );

        AlumnoResponse response = new AlumnoResponse(
                1L,
                "Maria Gomez Ramos",
                "maria@test.com",
                "20250001",
                "05/11/2026",
                List.of(),
                BigDecimal.ZERO
        );

        when(alumnoService.registrar(any(AlumnoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Maria Gomez Ramos"))
                .andExpect(jsonPath("$.email").value("maria@test.com"));

        verify(alumnoService).registrar(any(AlumnoRequest.class));
    }

    @Test
    void obtenerPorId_alumnoExistente_retornaAlumnoConPromedio() throws Exception {
        Long id = 1L;

        AlumnoResponse response = new AlumnoResponse(
                id,
                "Juan Perez Lopez",
                "juan@test.com",
                "20250001",
                "05/11/2026",
                List.of(),
                new BigDecimal("8.5")
        );

        when(alumnoService.obtenerPorId(id)).thenReturn(response);

        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez Lopez"))
                .andExpect(jsonPath("$.promedio").value(8.5));

        verify(alumnoService).obtenerPorId(id);
    }

    @Test
    void obtenerPorId_idInexistente_retorna404() throws Exception {
        Long id = 999999L;

        when(alumnoService.obtenerPorId(id))
                .thenThrow(new RecursoNoEncontradoException(
                        "Alumno no encontrado con id: " + id
                ));

        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Alumno no encontrado con id: " + id));

        verify(alumnoService).obtenerPorId(id);
    }

    @Test
    void listar_conAlumnos_retornaLista() throws Exception {
        List<AlumnoResponse> lista = List.of(
                new AlumnoResponse(
                        1L,
                        "Juan Perez Lopez",
                        "juan@test.com",
                        "20250001",
                        "05/11/2026",
                        List.of(),
                        BigDecimal.ZERO
                )
        );

        when(alumnoService.lista()).thenReturn(lista);

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez Lopez"));

        verify(alumnoService).lista();
    }

    @Test
    void actualizar_datosValidos_retornaAlumnoActualizado() throws Exception {
        Long id = 1L;

        AlumnoRequest request = new AlumnoRequest(
                "Juan",
                "Perez",
                "Ramos"
        );

        AlumnoResponse response = new AlumnoResponse(
                id,
                "Juan Perez Ramos",
                "juan@test.com",
                "20250001",
                "05/11/2026",
                List.of(),
                BigDecimal.ZERO
        );

        when(alumnoService.actualizar(any(AlumnoRequest.class), eq(id)))
                .thenReturn(response);

        mockMvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez Ramos"));

        verify(alumnoService).actualizar(any(AlumnoRequest.class), eq(id));
    }

    @Test
    void eliminar_sinInscripciones_eliminaAlumno() throws Exception {
        Long id = 1L;

        doNothing().when(alumnoService).eliminar(id);

        mockMvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(alumnoService).eliminar(id);
    }
}
