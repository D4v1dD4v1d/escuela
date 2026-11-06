package com.david.escuela.services.calificaciones;

import com.david.escuela.dto.calificaciones.CalificacionRequest;
import com.david.escuela.dto.calificaciones.CalificacionResponse;
import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.entities.Calificacion;
import com.david.escuela.entities.Inscripcion;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.CalificacionMapper;
import com.david.escuela.repositories.CalificacionRepository;
import com.david.escuela.repositories.InscripcionRepository;
import com.david.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalificacionServiceImpl implements CalificacionService{
    private final CalificacionRepository calificacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CalificacionMapper calificacionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> lista() {
        log.info("Listando aulas...");

        return calificacionRepository.findAll().stream()
                .map(calificacionMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {
        log.info("Obteniendo calificación con id: {}",id);

        return calificacionMapper.entidadAResponse(obtenerCalificacion(id));
    }

    @Override
    public CalificacionResponse registrar(CalificacionRequest request) {
        validarDatosUnicos(request);

        log.info("Registrando calificación...");

        Inscripcion inscripcion =ServiceUtils.obtenerEntidadOException(inscripcionRepository,request.idInscripcion(), Inscripcion.class);

        Calificacion calificacion = calificacionMapper.requestAEntidad(request,inscripcion);

        calificacionRepository.saveAndFlush(calificacion);

        log.info("Calificación registrada con éxito");

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public CalificacionResponse actualizar(CalificacionRequest request, Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Actualizando calificación...");

        validarCambiosUnicos(request,id);

        Inscripcion inscripcion = ServiceUtils.obtenerEntidadOException(inscripcionRepository,id, Inscripcion.class);

        calificacion.actualizar(request.calificacion(),inscripcion);

        calificacionRepository.saveAndFlush(calificacion);

        log.info("Calificación actualizada {}.", calificacion.getCalificacion());

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public void eliminar(Long id) {
        Calificacion calificacion = obtenerCalificacion(id);

        log.info("Eliminando calificaión...");

        calificacionRepository.delete(calificacion);
        calificacionRepository.flush();

        log.info("Calificación {} eliminada correctamente.", calificacion.getCalificacion());
    }

    private Calificacion obtenerCalificacion(Long id){
        log.info("Obteniendo calificacion...");
        return ServiceUtils.obtenerEntidadOException(calificacionRepository,id,Calificacion.class);
    }
    private void validarDatosUnicos(CalificacionRequest request){
        if (calificacionRepository.existsByInscripcionId(request.idInscripcion()))
            throw new ConflictoException("El alumno ya tiene una inscripción en este grupo.");
    }
    private void validarCambiosUnicos(CalificacionRequest request,Long id){
        if (calificacionRepository.existsByInscripcionIdAndIdNot(request.idInscripcion(),id))
            throw new ConflictoException("El alumno ya tiene una inscripción en este grupo.");
    }
}
