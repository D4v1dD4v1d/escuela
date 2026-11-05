package com.david.escuela.services.maestros;

import com.david.escuela.dto.maestros.MaestroRequest;
import com.david.escuela.dto.maestros.MaestroResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface MaestroService extends CrudService<MaestroRequest, MaestroResponse> {
    @Override
    List<MaestroResponse> lista();

    @Override
    MaestroResponse obtenerPorId(Long id);

    @Override
    MaestroResponse registrar(MaestroRequest request);

    @Override
    MaestroResponse actualizar(MaestroRequest request, Long id);

    @Override
    void eliminar(Long id);
}
