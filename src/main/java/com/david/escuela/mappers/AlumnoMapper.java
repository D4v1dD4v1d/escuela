package com.david.escuela.mappers;

import com.david.escuela.dto.alumnos.AlumnoRequest;
import com.david.escuela.dto.alumnos.AlumnoResponse;
import com.david.escuela.dto.datos.DatosAlumno;
import com.david.escuela.dto.datos.DatosCalificacion;
import com.david.escuela.entities.Alumno;
import com.david.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class AlumnoMapper implements CommonMapper<AlumnoRequest, Alumno, AlumnoResponse>{
    @Override
    public Alumno requestAEntidad(AlumnoRequest request) {
        return request == null
                ? null
                : Alumno.crear(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno());
    }
    public Alumno requestAEntidad(AlumnoRequest request, String email, String matricula) {

        if(request == null) return null;

        Alumno alumno = requestAEntidad(request);

        alumno.asignarDatosAcademicos(email, matricula);

        return alumno;
    }
    @Override
    public AlumnoResponse entidadAResponse(Alumno entidad) {
        return entidad == null
                ? null
                : new AlumnoResponse(
                        entidad.getId(),
                        entidad.obtenerNombreCompleto(),
                        entidad.getEmail(),
                        entidad.getMatricula(),
                        StringCustomUtils.localDateAString(
                                entidad.getFechaIngreso()),
                        entidadADatosCalificacion(entidad),
                        entidad.obtenerPromedio());
    }

    private List<DatosCalificacion> entidadADatosCalificacion(Alumno entidad){

        if(entidad == null || entidad.getInscripciones() == null || entidad.getInscripciones().isEmpty())
            return List.of();

        return entidad.getInscripciones().stream()
                .map(inscripcion ->  new DatosCalificacion(
                        inscripcion.getGrupo().getCurso().getNombre(),
                        inscripcion.getGrupo().getPeriodo(),
                        inscripcion.getCalificacion() != null
                                ? inscripcion.getCalificacion().getCalificacion()
                                : null
                ))
                .toList();
    }

    public DatosAlumno entidadADatos(Alumno entidad){
        return entidad == null
            ? null
            : new DatosAlumno(
                    entidad.obtenerNombreCompleto(),
                    entidad.getMatricula(),
                    entidad.getEmail(),
                    StringCustomUtils.localDateAString(entidad.getFechaIngreso()));
    }
}
