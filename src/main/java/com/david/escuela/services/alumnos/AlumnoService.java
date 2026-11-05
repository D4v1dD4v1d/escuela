package com.david.escuela.services.alumnos;

import com.david.escuela.dto.alumnos.AlumnoRequest;
import com.david.escuela.dto.alumnos.AlumnoResponse;
import com.david.escuela.services.CrudService;

import java.util.List;

public interface AlumnoService extends CrudService<AlumnoRequest, AlumnoResponse> {
    @Override
    List<AlumnoResponse> lista();

    @Override
    AlumnoResponse obtenerPorId(Long id);

    @Override
    AlumnoResponse registrar(AlumnoRequest request);

    @Override
    AlumnoResponse actualizar(AlumnoRequest request, Long id);

    @Override
    void eliminar(Long id);
}
