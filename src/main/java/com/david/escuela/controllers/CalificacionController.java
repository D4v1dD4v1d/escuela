package com.david.escuela.controllers;

import com.david.escuela.dto.calificaciones.CalificacionRequest;
import com.david.escuela.dto.calificaciones.CalificacionResponse;
import com.david.escuela.services.calificaciones.CalificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calificaciones")
@Tag(name = "API calificaciones", description = "Métodos para gestión de las calificaiones")

public class CalificacionController extends CrudController<CalificacionRequest, CalificacionResponse, CalificacionService> {
    public CalificacionController(CalificacionService service) {
        super(service);
    }
}
