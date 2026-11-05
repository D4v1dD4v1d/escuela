package com.david.escuela.controllers;

import com.david.escuela.dto.alumnos.AlumnoRequest;
import com.david.escuela.dto.alumnos.AlumnoResponse;
import com.david.escuela.services.alumnos.AlumnoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alumnos")
@Tag(name = "API alumnos", description = "Métodos para gestión de alumnos")
public class AlumnoController extends CrudController<AlumnoRequest, AlumnoResponse, AlumnoService>{

    public AlumnoController(AlumnoService service) {
        super(service);
    }
}
