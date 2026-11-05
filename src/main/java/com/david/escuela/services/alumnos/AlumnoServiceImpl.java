package com.david.escuela.services.alumnos;

import com.david.escuela.dto.alumnos.AlumnoRequest;
import com.david.escuela.dto.alumnos.AlumnoResponse;
import com.david.escuela.entities.Alumno;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.AlumnoMapper;
import com.david.escuela.repositories.AlumnoRepository;
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
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoMapper alumnoMapper;
    private final AlumnoRepository alumnoRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> lista() {

        log.info("Listando alumnos...");

        return alumnoRepository.findAll().stream()
                .map(alumnoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {

        log.info("Buscando alumno con id: {}...",id);

        return alumnoMapper.entidadAResponse(obtenerAlumno(id));
    }

    @Override
    public AlumnoResponse registrar(AlumnoRequest request) {

        log.info("Registrando nuevo alumno...");

        Alumno alumno = alumnoMapper.requestAEntidad(
                request,
                generarEmail(request),
                generarMatricula(request));

        alumnoRepository.saveAndFlush(alumno);

        log.info("Nuevo alumno registrado: {}",alumno.obtenerNombreCompleto());

        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public AlumnoResponse actualizar(AlumnoRequest request, Long id) {

        Alumno alumno = obtenerAlumno(id);

        if(alumno.cambioEnDatos(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno()
        )){
            log.info("Actualizando alumno  {}.", alumno.obtenerNombreCompleto());

            alumno.actualizar(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno(),
                    alumno.getEmail(),
                    alumno.getMatricula()
            );

            alumnoRepository.saveAndFlush(alumno);
            log.info("Alumno {} actualizado con éxito.", alumno.obtenerNombreCompleto());
        }

        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public void eliminar(Long id) {
        Alumno alumno = obtenerAlumno(id);

        log.info("Borrando alumno {}...",alumno.obtenerNombreCompleto());

        if(inscripcionRepository.existsByAlumnoId(id))
            throw new EntidadRelacionada(
                    "No se puede borrar el alumno porque " +
                            "esta inscrito en uno o más cursos.");

        alumnoRepository.delete(alumno);
        alumnoRepository.flush();

        log.info("Alumno {} ha sido borrado con éxito.",alumno.obtenerNombreCompleto());

    }

    private Alumno obtenerAlumno(Long id){
        log.info("Obteniendo alumno {}..,",id);
        return ServiceUtils.obtenerEntidadOException(alumnoRepository,id, Alumno.class);
    }

    private String generarEmail(AlumnoRequest request){

        log.info("Generando email...");

        return alumnoRepository.generarEmail(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno());
    }
    private String generarMatricula(AlumnoRequest request){

        log.info("Generando matrícula...");

        return alumnoRepository.generarMatricula(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno());
    }
}
