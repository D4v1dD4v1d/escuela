package com.david.escuela.services.horarios;

import com.david.escuela.dto.horarios.HorarioRequest;
import com.david.escuela.dto.horarios.HorarioResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface HorarioService extends CrudService<HorarioRequest, HorarioResponse> {
    @Override
    List<HorarioResponse> lista();

    @Override
    HorarioResponse obtenerPorId(Long id);

    @Override
    HorarioResponse registrar(HorarioRequest request);

    @Override
    HorarioResponse actualizar(HorarioRequest request, Long id);

    @Override
    void eliminar(Long id);
}
