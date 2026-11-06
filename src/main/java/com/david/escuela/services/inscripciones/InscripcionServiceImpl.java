package com.david.escuela.services.inscripciones;

import com.david.escuela.dto.inscripciones.InscripcionRequest;
import com.david.escuela.dto.inscripciones.InscripcionResponse;
import com.david.escuela.entities.Alumno;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Inscripcion;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.InscripcionMapper;
import com.david.escuela.repositories.AlumnoRepository;
import com.david.escuela.repositories.CalificacionRepository;
import com.david.escuela.repositories.GrupoReposiroty;
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
public class InscripcionServiceImpl implements InscripcionService{
    private final InscripcionMapper inscripcionMapper;

    private final InscripcionRepository inscripcionRepository;
    private final AlumnoRepository alumnoRepository;
    private final GrupoReposiroty grupoReposiroty;
    private final CalificacionRepository calificacionRepository;

    @Override
    public List<InscripcionResponse> lista() {
        log.info("Listando inscripciones...");

        return inscripcionRepository.findAll().stream()
                .map(inscripcionMapper::entidadAResponse)
                .toList();
    }

    @Override
    public InscripcionResponse obtenerPorId(Long id) {
        log.info("Obteniendo incripción con id: {}.",id);
        return inscripcionMapper.entidadAResponse(obtenerInscripcion(id));
    }

    @Override
    public InscripcionResponse registrar(InscripcionRequest request) {

        log.info("Registrando inscripción...");
        Alumno alumno = ServiceUtils.obtenerEntidadOException(alumnoRepository,request.idAlumno(), Alumno.class);

        Grupo grupo = ServiceUtils.obtenerEntidadOException(grupoReposiroty,request.idGrupo(), Grupo.class);

        validarDatosUnicos(request);

        Inscripcion inscripcion = inscripcionMapper.requestAEntidad(request,alumno,grupo);

        inscripcionRepository.saveAndFlush(inscripcion);

        log.info("Alumno {} inscrito al grupo con id {} exitosamente.", alumno.obtenerNombreCompleto(),grupo.getId());
        return null;
    }

    @Override
    public InscripcionResponse actualizar(InscripcionRequest request, Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);

        Alumno alumno = ServiceUtils.obtenerEntidadOException(alumnoRepository,request.idAlumno(), Alumno.class);

        Grupo grupo = ServiceUtils.obtenerEntidadOException(grupoReposiroty,request.idGrupo(), Grupo.class);

        validarCambiosUnicos(request,id);

        inscripcion.actualizar(alumno,grupo);

        inscripcionRepository.saveAndFlush(inscripcion);

        log.info("Inscripcion actualizada...");
        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public void eliminar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        log.info("Eliminando inscripción con id: {}.", id);
        if(calificacionRepository.existsByInscripcionId(id))
            throw new EntidadRelacionada("No se puede eliminar la inscripción porque tiene una calificación asignada.");
        inscripcionRepository.delete(inscripcion);
        inscripcionRepository.flush();
        log.info("Inscripción con id: {} eliminada correctamente.",id);
    }

    private Inscripcion obtenerInscripcion(Long id){
        log.info("Obteniendo inscripción...");
        return ServiceUtils.obtenerEntidadOException(inscripcionRepository,id,Inscripcion.class);
    }

    private void validarDatosUnicos(InscripcionRequest request){
        if (inscripcionRepository.existsByGrupoIdAndAlumnoId(request.idGrupo(),request.idAlumno()))
            throw new ConflictoException("El alumno ya tiene una inscripción en este grupo.");
    }
    private void validarCambiosUnicos(InscripcionRequest request,Long id){
        if (inscripcionRepository.existsByGrupoIdAndAlumnoIdAndIdNot(request.idGrupo(),request.idAlumno(),id))
            throw new ConflictoException("El alumno ya tiene una inscripción en este grupo.");
    }


}
