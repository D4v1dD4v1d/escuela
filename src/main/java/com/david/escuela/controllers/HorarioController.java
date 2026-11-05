package com.david.escuela.controllers;

import com.david.escuela.dto.horarios.HorarioRequest;
import com.david.escuela.dto.horarios.HorarioResponse;
import com.david.escuela.services.horarios.HorarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "API horarios", description = "Métodos para gestión de los horarios")

public class HorarioController extends CrudController<HorarioRequest, HorarioResponse, HorarioService> {
    public HorarioController(HorarioService service) {
        super(service);
    }
}
