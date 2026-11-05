package com.david.escuela.mappers;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.aulas.AulaResponse;
import com.david.escuela.dto.datos.DatosAula;
import com.david.escuela.entities.Aula;
import org.springframework.stereotype.Component;

@Component
public class AulaMapper implements CommonMapper <AulaRequest, Aula, AulaResponse> {
    @Override
    public Aula requestAEntidad(AulaRequest request) {
        return request == null
                ? null
                : Aula.crear(
                        request.nombre(),
                        request.capacidad());
    }

    @Override
    public AulaResponse entidadAResponse(Aula entidad) {
        return entidad == null
                ? null
                : new AulaResponse(
                        entidad.getId(),
                        entidad.getNombre(),
                        entidad.getCapacidad());
    }

    public DatosAula entidadADatos(Aula entidad){
        return entidad == null
                ? null
                : new DatosAula(
                        entidad.getNombre(),
                        entidad.getCapacidad());

    }
}
