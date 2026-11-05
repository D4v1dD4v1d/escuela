package com.david.escuela.controllers;

import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.dto.inscripciones.InscripcionResponse;
import com.david.escuela.services.inscripciones.InscripcionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "API inscripciones", description = "Métodos para gestión de las inscripciones")

public class InscripcionController extends CrudController<InscripcionRequest, InscripcionResponse, InscripcionService>{
    public InscripcionController(InscripcionService service) {
        super(service);
    }
}
