package com.david.escuela.mappers;

import com.david.escuela.dto.datos.DatosInscripcion;
import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.dto.inscripciones.InscripcionResponse;
import com.david.escuela.entities.Alumno;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Inscripcion;
import com.david.escuela.utils.ServiceUtils;
import com.david.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class InscripcionMapper implements CommonMapper<InscripcionRequest, Inscripcion, InscripcionResponse>{
    private final AlumnoMapper alumnoMapper;
    private final GrupoMapper grupoMapper;

    @Override
    public Inscripcion requestAEntidad(InscripcionRequest request) {
        return request == null
                ? null
                : Inscripcion.crear();
    }

    public Inscripcion requestAEntidad(InscripcionRequest request, Alumno alumno, Grupo grupo) {
        if (request == null) return null;

        Inscripcion inscripcion = requestAEntidad(request);

        inscripcion.asignarAlumnoGrupo(alumno,grupo);

        return inscripcion;
    }

    @Override
    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        return entidad == null
                ? null
                : new InscripcionResponse(
                        entidad.getId(),
                        alumnoMapper.entidadADatos(entidad.getAlumno()),
                        grupoMapper.entidadADatos(entidad.getGrupo()),
                        entidad.getCalificacion().getCalificacion(),
                        StringCustomUtils.localDateAString(entidad.getFechaInscripcion()));
    }

    public DatosInscripcion entidadADatos(Inscripcion entidad){
        return entidad == null
                ? null
                : new DatosInscripcion(
                        alumnoMapper.entidadADatos(entidad.getAlumno()),
                        grupoMapper.entidadADatos(entidad.getGrupo()),
                        StringCustomUtils.localDateAString(entidad.getFechaInscripcion()));
    }
}
