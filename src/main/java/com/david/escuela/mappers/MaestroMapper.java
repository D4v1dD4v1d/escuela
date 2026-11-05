package com.david.escuela.mappers;

import com.david.escuela.dto.datos.DatosCurso;
import com.david.escuela.dto.datos.DatosMaestro;
import com.david.escuela.dto.maestros.MaestroRequest;
import com.david.escuela.dto.maestros.MaestroResponse;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Maestro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaestroMapper implements CommonMapper<MaestroRequest, Maestro, MaestroResponse> {

    private final CursoMapper cursoMapper;

    @Override
    public Maestro requestAEntidad(MaestroRequest request) {
        return request ==null
                ? null
                : Maestro.crear(
                        request.nombre(),
                        request.apellidoPaterno(),
                        request.apellidoMaterno(),
                        request.email(),
                        request.telefono()
                );
    }

    @Override
    public MaestroResponse entidadAResponse(Maestro entidad) {
        return entidad ==null
                ? null
                :new MaestroResponse(
                        entidad.getId(),
                        entidad.obtenerNombreCompleto(),
                        entidad.getEmail(),
                        entidad.getTelefono(),
                        entidadADatosCurso(entidad));
    }

    public List<DatosCurso> entidadADatosCurso(Maestro entidad){
        return entidad == null
                ?List.of()
                :entidad.getGrupos().stream()
                .map(Grupo::getCurso)
                .map(cursoMapper::entidadADatos)
                .toList();
    }

    public DatosMaestro entidadADatos(Maestro entidad){
        return entidad == null
                ? null
                : new DatosMaestro(
                        entidad.obtenerNombreCompleto(),
                        entidad.getEmail(),
                        entidad.getTelefono());
    }
}
