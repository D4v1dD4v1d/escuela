package com.david.escuela.services.grupos;

import com.david.escuela.dto.grupos.GrupoRequest;
import com.david.escuela.dto.grupos.GrupoResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface GrupoService extends CrudService<GrupoRequest, GrupoResponse> {
    @Override
    List<GrupoResponse> lista();

    @Override
    GrupoResponse obtenerPorId(Long id);

    @Override
    GrupoResponse registrar(GrupoRequest request);

    @Override
    GrupoResponse actualizar(GrupoRequest request, Long id);

    @Override
    void eliminar(Long id);
}
