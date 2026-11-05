package com.david.escuela.mappers;

import com.david.escuela.dto.datos.DatosGrupo;
import com.david.escuela.dto.grupos.GrupoRequest;
import com.david.escuela.dto.grupos.GrupoResponse;
import com.david.escuela.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GrupoMapper implements CommonMapper<GrupoRequest, Grupo, GrupoResponse>{
    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;
    private final HorarioMapper horarioMapper;

    @Override
    public Grupo requestAEntidad(GrupoRequest request) {
        return request == null
                ? null
                : Grupo.crear(
                        request.periodo());
    }

    public Grupo requestAEntidad(GrupoRequest request, Curso curso, Maestro maestro, Aula aula) {
        if (request== null) return null;

        Grupo grupo = requestAEntidad(request);

        grupo.asignarObjetos(maestro,curso,aula);

        return grupo;
    }

    @Override
    public GrupoResponse entidadAResponse(Grupo entidad) {
        return entidad == null
                ? null
                : new GrupoResponse(
                        entidad.getId(),
                        cursoMapper.entidadADatos(entidad.getCurso()),
                        maestroMapper.entidadADatos(entidad.getMaestro()),
                        aulaMapper.entidadADatos(entidad.getAula()),
                        entidad.getHorarios().stream()
                                .map(horarioMapper::entidadAString)
                                .toList(),
                        entidad.getPeriodo());
    }


    public DatosGrupo entidadADatos(Grupo entidad){
        return entidad == null
                ? null
                : new DatosGrupo(
                        entidad.getCurso().getNombre(),
                        entidad.getMaestro().obtenerNombreCompleto(),
                        entidad.getAula().getNombre(),
                        entidad.getPeriodo());
    }

}
