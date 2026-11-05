package com.david.escuela.services.horarios;

import com.david.escuela.dto.horarios.HorarioRequest;
import com.david.escuela.dto.horarios.HorarioResponse;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Horario;
import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.DatoInvalidoException;
import com.david.escuela.mappers.HorarioMapper;
import com.david.escuela.repositories.GrupoReposiroty;
import com.david.escuela.repositories.HorarioRepository;
import com.david.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HorarioServiceImpl implements HorarioService{
    private final HorarioMapper horarioMapper;

    private final HorarioRepository horarioRepository;
    private final GrupoReposiroty grupoReposiroty;

    @Override
    public List<HorarioResponse> lista() {
        log.info("Listando grupos...");

        return horarioRepository.findAll().stream()
                .map(horarioMapper::entidadAResponse)
                .toList();
    }

    @Override
    public HorarioResponse obtenerPorId(Long id) {
        log.info("Obteniendo Horario...");
        Horario horario = obtenerHorario(id);
        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public HorarioResponse registrar(HorarioRequest request) {

        validarFormatoHorario(request.horaInicio());
        validarFormatoHorario(request.horaFin());
        validarInicioMenorQueFin(request.horaInicio(),request.horaFin());

        log.info("Registrando horario...");
        Grupo grupo = ServiceUtils.obtenerEntidadOException(grupoReposiroty,request.idGrupo(),Grupo.class);

        List <String> lista = horarioRepository.buscarHorariosFormateados(
                request.horaInicio(),
                request.horaFin(),
                request.dia(),
                request.idGrupo(),
                grupo.getAula().getId(),
                grupo.getPeriodo());


        if (!lista.isEmpty())
            throw new ConflictoException("Ese horario ya esta ocupado, seleccione otro.");

        Horario horario =horarioMapper.requestAEntidad(request,grupo);

        horarioRepository.saveAndFlush(horario);

        log.info("Horario {} registrado exitosamente.",
                        horario.formatoHora(request.dia().toString(),
                        horario.getHoraInicio(),
                        horario.getHoraFin()));

        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Horario horario = obtenerHorario(id);

        log.info("Actualizando horario con id: {}...",id);

        validarFormatoHorario(request.horaFin());
        validarFormatoHorario(request.horaInicio());
        validarInicioMenorQueFin(request.horaInicio(),request.horaFin());

        Grupo grupo = grupoReposiroty.findById(request.idGrupo()).
                orElseThrow(() ->new ConflictoException("El Grupo con id: "+ request.idGrupo()+" no existe."));

        List <String> lista = horarioRepository.buscarHorariosFormateados(
                request.horaInicio(),
                request.horaFin(),
                request.idGrupo(),
                grupo.getAula().getId(),
                id,
                grupo.getPeriodo(),
                request.dia());

        if (!lista.isEmpty())
            throw new ConflictoException("Ese horario ya esta ocupado, seleccione otro.");

        horario.actualizar(
                grupo,
                request.dia(),
                request.horaInicio(),
                request.horaFin());

        horarioRepository.saveAndFlush(horario);

        log.info("Horario con id {} actualizado correctamente",id);

        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public void eliminar(Long id) {

        Horario horario = obtenerHorario(id);
        log.info("Eliminando horario con id: {}.", id);
        horarioRepository.delete(horario);
        horarioRepository.flush();
        log.info("Horario con id: {} eliminado correctamente.",id);

    }

    private Horario obtenerHorario(Long id){
        log.info("Obteniendo horario...");
        return ServiceUtils.obtenerEntidadOException(horarioRepository,id,Horario.class);
    }
    private void validarInicioMenorQueFin(String inicio, String fin){
        LocalTime horaInicio = LocalTime.parse(inicio);
        LocalTime horaFin = LocalTime.parse(fin);

        if(horaFin.isBefore(horaInicio.plusHours(1)))
            throw new DatoInvalidoException("La hora de inicio debe ser menor que la hora de fin" +
                    " y debe tener una duración de al menos una hora.");


    }
    private void validarFormatoHorario(String hora){
        if (hora== null)
            throw new ConflictoException("La hora es requerida");
        if (!hora.matches("^([01]\\d|2[0-3]):([0-5]\\d)$"))
            throw new ConflictoException("La hora debe tener el formato HH-MM");
    }
}
