package com.david.escuela.controllers;

import com.david.escuela.dto.cursos.CursoRequest;
import com.david.escuela.dto.cursos.CursoResponse;
import com.david.escuela.services.cursos.CursoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "API cursos", description = "Métodos para gestión de los cursos")

public class CursoController extends CrudController<CursoRequest, CursoResponse, CursoService> {
    public CursoController(CursoService service) {
        super(service);
    }
}
