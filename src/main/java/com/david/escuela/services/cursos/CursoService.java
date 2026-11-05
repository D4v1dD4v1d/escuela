package com.david.escuela.services.cursos;

import com.david.escuela.dto.cursos.CursoRequest;
import com.david.escuela.dto.cursos.CursoResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface CursoService extends CrudService<CursoRequest, CursoResponse> {
    @Override
    List<CursoResponse> lista();

    @Override
    CursoResponse obtenerPorId(Long id);

    @Override
    CursoResponse registrar(CursoRequest request);

    @Override
    CursoResponse actualizar(CursoRequest request, Long id);

    @Override
    void eliminar(Long id);
}
