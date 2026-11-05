package com.david.escuela.services.inscripciones;

import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.dto.inscripciones.InscripcionResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface InscripcionService extends CrudService<InscripcionRequest, InscripcionResponse> {
    @Override
    List<InscripcionResponse> lista();

    @Override
    InscripcionResponse obtenerPorId(Long id);

    @Override
    InscripcionResponse registrar(InscripcionRequest request);

    @Override
    InscripcionResponse actualizar(InscripcionRequest request, Long id);

    @Override
    void eliminar(Long id);
}
