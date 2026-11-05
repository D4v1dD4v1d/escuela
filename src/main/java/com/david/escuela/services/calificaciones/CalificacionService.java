package com.david.escuela.services.calificaciones;

import com.david.escuela.dto.calificaciones.CalificacionRequest;
import com.david.escuela.dto.calificaciones.CalificacionResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface CalificacionService extends CrudService<CalificacionRequest, CalificacionResponse> {
    @Override
    List<CalificacionResponse> lista();

    @Override
    CalificacionResponse obtenerPorId(Long id);

    @Override
    CalificacionResponse registrar(CalificacionRequest request);

    @Override
    CalificacionResponse actualizar(CalificacionRequest request, Long id);

    @Override
    void eliminar(Long id);
}
