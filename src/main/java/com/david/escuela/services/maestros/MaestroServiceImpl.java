package com.david.escuela.services.maestros;

import com.david.escuela.dto.maestros.MaestroRequest;
import com.david.escuela.dto.maestros.MaestroResponse;
import com.david.escuela.entities.Maestro;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.EntidadRelacionada;
import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.mappers.MaestroMapper;
import com.david.escuela.repositories.GrupoReposiroty;
import com.david.escuela.repositories.MaestroRepository;
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
public class MaestroServiceImpl implements MaestroService{

    private final MaestroMapper maestroMapper;
    private final MaestroRepository maestroRepository;
    private final GrupoReposiroty grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MaestroResponse> lista() {

        log.info("Listando maestros...");

        return maestroRepository.findAll().stream()
                .map(maestroMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorId(Long id) {

        log.info("Buscando maestro con id: {}...",id);

        return maestroMapper.entidadAResponse(obtenerMaestro(id));
    }

    @Override
    public MaestroResponse registrar(MaestroRequest request) {

        log.info("Registrando nuevo maestro...");

        validarDatosUnicos(request);

        Maestro maestro =maestroMapper.requestAEntidad(request);

        maestroRepository.saveAndFlush(maestro);

        log.info("Nuevo maestro registrado: {}.", maestro.obtenerNombreCompleto());

        return maestroMapper.entidadAResponse(maestro);

    }

    @Override
    public MaestroResponse actualizar(MaestroRequest request, Long id) {

        Maestro maestro = obtenerMaestro(id);

        log.info("Actualizando al maestro con id: {}...",id);

        validarCambiosUnicos(request, id);

        maestro.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );

        maestroRepository.saveAndFlush(maestro);

        log.info("Maestro {} actualizado con éxito.",maestro.obtenerNombreCompleto());

        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public void eliminar(Long id) {

        Maestro maestro = obtenerMaestro(id);

        log.info("Eliminando al maestro con id: {}...",id);

        if (grupoRepository.existsByMaestroId(id))
            throw new EntidadRelacionada("El maestro no se puede eliminar porque tiene grupos asignados.");

        maestroRepository.delete(maestro);

        maestroRepository.flush();

        log.info("Maestro con id: {} eliminado correctamente.",id);
    }



    private void validarDatosUnicos(MaestroRequest request){

        log.info("Validando télefono único...");

        if(maestroRepository.existsByTelefono(request.telefono().trim()))
            throw new ConflictoException("Ya existe un maestro con el télefono de :"+request.telefono());

        log.info("Validando email único...");

        if(maestroRepository.existsByEmailIgnoreCase(request.email().trim()))
            throw new ConflictoException("Ya existe un maestro con el email de :"+request.email());

    }


    private void validarCambiosUnicos(MaestroRequest request, Long id){

        log.info("Validando cambio en télefono único...");

        if(maestroRepository.existsByTelefonoAndId(request.telefono().trim(),id))
            throw new ConflictoException("Ya existe un maestro con el telefono de :"+request.telefono());

        log.info("Validando cambio en email único...");

        if(maestroRepository.existsByEmailIgnoreCaseAndIdNot(request.email().trim(),id))
            throw new ConflictoException("Ya existe un maestro con el email :"+request.email());
    }


    private Maestro obtenerMaestro(Long id){
        log.info("Obteniendo Maestro {}",id);
        return ServiceUtils.obtenerEntidadOException(maestroRepository,id,Maestro.class);
    }
}
