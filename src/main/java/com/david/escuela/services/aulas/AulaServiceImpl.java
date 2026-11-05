package com.david.escuela.services.aulas;

import com.david.escuela.dto.aulas.AulaRequest;
import com.david.escuela.dto.aulas.AulaResponse;
import com.david.escuela.entities.Aula;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.mappers.AulaMapper;
import com.david.escuela.repositories.AulaRepository;
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
public class AulaServiceImpl implements AulaService{

    private final AulaMapper aulaMapper;
    private final AulaRepository aulaRepository;
    private final GrupoReposiroty grupoReposiroty;

    @Override
    @Transactional(readOnly = true)
    public List<AulaResponse> lista() {
        log.info("Listando aulas...");

        return aulaRepository.findAll().stream()
                .map(aulaMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AulaResponse obtenerPorId(Long id) {
        log.info("Obteniendo aula con id: ",id);

        return aulaMapper.entidadAResponse(obtenerAula(id));
    }

    @Override
    public AulaResponse registrar(AulaRequest request) {
        log.info("Registrando aula...");

        validarDatosUnicos(request);

        Aula aula = aulaMapper.requestAEntidad(request);

        aulaRepository.saveAndFlush(aula);

        log.info("Nueva aula registrada: {}.",aula.getNombre());

        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    public AulaResponse actualizar(AulaRequest request, Long id) {
        Aula aula = obtenerAula(id);

        log.info("Actualizando aula don id: {}...",id);

        validarCambiosUnicos(request,id);

        aula.actualizar(
                request.nombre(),
                request.capacidad()
        );

        aulaRepository.saveAndFlush(aula);

        log.info("Aula {} actualizada con éxito.",aula.getNombre());

        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    public void eliminar(Long id) {
        Aula aula = obtenerAula(id);

        log.info("Eliminando aula con id: {}...",id);

        if (grupoReposiroty.existsByAulaId(id))
            throw new EntidadRelacionada("No se puede eliminar el aula por que hay grupos que la ocupan.");

        aulaRepository.delete(aula);
        aulaRepository.flush();

        log.info("Aula: {} eliminada correctamente.",aula.getNombre());
    }

    private void validarDatosUnicos(AulaRequest request) {

        log.info("Registrando aula...");

        if (aulaRepository.existsByNombreIgnoreCase(request.nombre().trim()))
            throw new ConflictoException("Ya existe un aula con el nombre de :" + request.nombre());
    }

    private void validarCambiosUnicos(AulaRequest request, Long id) {

        log.info("Validando cambio en aula...");

        if (aulaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(),id))
            throw new ConflictoException("Ya existe un aula con el nombre de :" + request.nombre());
    }

    private Aula obtenerAula(Long id){
        log.info("Obteniendo aula {}...", id);
        return ServiceUtils.obtenerEntidadOException(aulaRepository,id, Aula.class);
    }
}
