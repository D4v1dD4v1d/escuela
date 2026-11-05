package com.david.escuela.mappers;

import com.david.escuela.dto.calificaciones.CalificacionRequest;
import com.david.escuela.dto.calificaciones.CalificacionResponse;
import com.david.escuela.entities.Calificacion;
import com.david.escuela.entities.Inscripcion;
import com.david.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class CalificacionMapper implements CommonMapper<CalificacionRequest, Calificacion, CalificacionResponse> {
    private final InscripcionMapper inscripcionMapper;

    @Override
    public Calificacion requestAEntidad(CalificacionRequest request) {
        return request == null
                ? null
                : Calificacion.crear(
                      request.calificacion());
    }

    public Calificacion requestAEntidad(CalificacionRequest request, Inscripcion inscripcion) {
        if (inscripcion== null) return null;

        Calificacion calificacion= requestAEntidad(request);

        calificacion.asignarInscripcion(inscripcion);

        return calificacion;
    }
    @Override
    public CalificacionResponse entidadAResponse(Calificacion entidad) {
        return entidad == null
                ? null
                : new CalificacionResponse(
                        entidad.getId(),
                        inscripcionMapper.entidadADatos(entidad.getInscripcion()),
                        entidad.getCalificacion(),
                        StringCustomUtils.localDateAString(entidad.getFechaRegistro()));
    }
}
