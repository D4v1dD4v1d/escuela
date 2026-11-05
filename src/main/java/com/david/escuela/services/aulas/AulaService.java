package com.david.escuela.services.aulas;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.aulas.AulaResponse;
import com.david.escuela.entities.Aula;
import com.david.escuela.services.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AulaService extends CrudService<AulaRequest, AulaResponse> {
    @Override
    List<AulaResponse> lista();

    @Override
    AulaResponse obtenerPorId(Long id);

    @Override
    AulaResponse registrar(AulaRequest request);

    @Override
    AulaResponse actualizar(AulaRequest request, Long id);

    @Override
    void eliminar(Long id);
}
