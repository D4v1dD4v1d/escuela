package com.david.escuela.services.cursos;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.cursos.CursoRequest;
import com.david.escuela.dto.cursos.CursoResponse;
import com.david.escuela.entities.Curso;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.CursoMapper;
import com.david.escuela.repositories.CursoRepository;
import com.david.escuela.repositories.GrupoReposiroty;
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
public class CursoServiceImpl implements CursoService{
    private final CursoMapper cursoMapper;
    private final CursoRepository cursoRepository;
    private final GrupoReposiroty grupoReposiroty;


    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> lista() {
        log.info("Listando cursos...");

        return cursoRepository.findAll().stream()
                .map(cursoMapper::entidadAResponse)
                .toList();
    }

    @Override
    public CursoResponse obtenerPorId(Long id) {
        log.info("Obteniendo curso con id: {}", id);

        return cursoMapper.entidadAResponse(obtenerCurso(id));
    }

    @Override
    public CursoResponse registrar(CursoRequest request) {
        log.info("Registrando curso...");

        validarDatosUnicos(request);

        Curso curso = cursoMapper.requestAEntidad(request);

        cursoRepository.saveAndFlush(curso);

        log.info("Nuevo curso registrado: {}.", curso.getNombre());

        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public CursoResponse actualizar(CursoRequest request, Long id) {
        Curso curso = obtenerCurso(id);

        log.info("Actualizando curso {}...",curso.getNombre());

        validarCambiosUnicos(request,id);

        curso.actualizar(
                request.nombre(),
                request.descripcion(),
                request.creditos()
        );

        cursoRepository.saveAndFlush(curso);

        log.info("Curso {} actualizado con éxito.",curso.getNombre());

        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public void eliminar(Long id) {
        Curso curso = obtenerCurso(id);

        if(grupoReposiroty.existsByCursoId(id))
            throw new EntidadRelacionada("No se puede borrar el curso porque " +
                    "1 o más grupos lo están tomando");

        cursoRepository.delete(curso);
        cursoRepository.flush();

        log.info("Curso {} eliminado exitosamente.",curso.getNombre());
    }

    private void validarDatosUnicos(CursoRequest request) {

        log.info("Registrando curso...");

        if (cursoRepository.existsByNombreIgnoreCase(request.nombre().trim()))
            throw new ConflictoException("Ya existe un curso con el nombre de :" + request.nombre());
    }

    private void validarCambiosUnicos(CursoRequest request, Long id) {

        log.info("Validando cambio en el curso...");

        if (cursoRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(),id))
            throw new ConflictoException("Ya existe un curso con el nombre de :" + request.nombre());
    }
    private Curso obtenerCurso(Long id){
        log.info("Obteniendo curso {}...",id);

        return ServiceUtils.obtenerEntidadOException(cursoRepository,id,Curso.class);
    }
}
