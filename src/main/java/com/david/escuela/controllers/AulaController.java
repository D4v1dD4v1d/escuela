package com.david.escuela.controllers;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.aulas.AulaResponse;
import com.david.escuela.services.aulas.AulaService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "API aulas", description = "Métodos para gestión de las aulas")

public class AulaController extends CrudController<AulaRequest, AulaResponse, AulaService>{
    public AulaController(AulaService service) {
        super(service);
    }
}
