package com.david.escuela.services.grupos;

import com.david.escuela.dto.grupos.GrupoRequest;
import com.david.escuela.dto.grupos.GrupoResponse;
import com.david.escuela.entities.Aula;
import com.david.escuela.entities.Curso;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Maestro;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.GrupoMapper;
import com.david.escuela.repositories.*;
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
public class GrupoServiceImpl implements GrupoService{
    private final GrupoMapper grupoMapper;
    private final GrupoReposiroty grupoRepository;
    private final CursoRepository cursoRepository;
    private final AulaRepository aulaRepository;
    private final MaestroRepository maestroRepository;
    private final InscripcionRepository inscripcionRepository;
    private final HorarioRepository horarioRepository;
    //validar que el aula exista antes de registrar o de actualizar
    @Override
    @Transactional(readOnly = true)
    public List<GrupoResponse> lista() {
        log.info("Listando grupos...");

        return grupoRepository.findAll().stream()
                .map(grupoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse obtenerPorId(Long id) {
        log.info("Obteniendo Grupo...");
        Grupo grupo = obtenerGrupo(id);
        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public GrupoResponse registrar(GrupoRequest request) {
        Curso curso = ServiceUtils.obtenerEntidadOException(cursoRepository,request.idCurso(),Curso.class);
        Maestro maestro = ServiceUtils.obtenerEntidadOException(maestroRepository,request.idMaestro(),Maestro.class);
        Aula aula = ServiceUtils.obtenerEntidadOException(aulaRepository,request.idAula(),Aula.class);

        validarFormatoPeriodo(request.periodo());

        log.info("Registrando Grupo...");

        validarDatosUnicos(request);

        Grupo grupo = grupoMapper.requestAEntidad(
                request,
                curso,
                maestro,
                aula);

        grupoRepository.saveAndFlush(grupo);

        log.info("Grupo {} registrado correctamente.",grupo.getId());


        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public GrupoResponse actualizar(GrupoRequest request, Long id) {
        Grupo grupo =obtenerGrupo(id);

        Curso curso = ServiceUtils.obtenerEntidadOException(cursoRepository,request.idCurso(),Curso.class);
        Maestro maestro = ServiceUtils.obtenerEntidadOException(maestroRepository,request.idMaestro(),Maestro.class);
        Aula aula = ServiceUtils.obtenerEntidadOException(aulaRepository,request.idAula(),Aula.class);

        log.info("Actualizando grupo con id: {}...",grupo.getId());

        validarFormatoPeriodo(request.periodo());

        validarCambiosUnicos(request, id);

        grupo.actualizar(
                curso,
                maestro,
                aula,
                request.periodo());

        grupoRepository.saveAndFlush(grupo);

        log.info("Grupo con id: {} ha sido actualizado correctamente.",id);

        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public void eliminar(Long id) {
        Grupo grupo =obtenerGrupo(id);
        if(inscripcionRepository.existsByGrupoId(id))
            throw new EntidadRelacionada("No se puede eliminar el grupo" +
                    " porque hay alumnos inscritos en el.");

        if (horarioRepository.existsByGrupoId(id))
            throw new EntidadRelacionada("No se puede eliminar el grupo" +
                    " porque tiene horarios.");

        grupoRepository.delete(grupo);

        grupoRepository.flush();

        log.info("El grupo con id: {} fue eliminado correctamente",id);

    }

    private Grupo obtenerGrupo(Long id){
        log.info("Obteniendo grupo...");
        return ServiceUtils.obtenerEntidadOException(grupoRepository,id,Grupo.class);
    }


    private void validarFormatoPeriodo(String periodo){
        if (periodo== null)
            throw new ConflictoException("El periodo es requerido");
        if (!periodo.matches("^\\d{4}-(0[1-9]|1[0-2])$"))
            throw new ConflictoException("El periodo debe tener el formato {YYYY-MM}");
    }

    private void validarCambiosUnicos(GrupoRequest request, Long idGrupo){
         if(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                request.idCurso(),
                request.idMaestro(),
                request.idAula(),
                request.periodo(),
                idGrupo))
             throw new ConflictoException("Ya existe un grupo con la relación " +
                     "combinación [Curso + Maestro + Aula + Periodo] y no puede haber " +
                     "2 iguales.");
    }

    private void validarDatosUnicos(GrupoRequest request){
         if(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
                request.idCurso(),
                request.idMaestro(),
                request.idAula(),
                request.periodo()))
             throw new ConflictoException("Ya existe un grupo con la relación " +
                     "combinación [Curso + Maestro + Aula + Periodo] y no puede haber " +
                     "2 iguales.");
    }


}
