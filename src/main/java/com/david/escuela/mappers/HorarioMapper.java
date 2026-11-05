package com.david.escuela.mappers;

import com.david.escuela.dto.datos.DatosGrupo;
import com.david.escuela.dto.datos.DatosHorarios;
import com.david.escuela.dto.horarios.HorarioRequest;
import com.david.escuela.dto.horarios.HorarioResponse;
import com.david.escuela.entities.Grupo;
import com.david.escuela.entities.Horario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HorarioMapper implements CommonMapper<HorarioRequest, Horario, HorarioResponse> {



    @Override
    public Horario requestAEntidad(HorarioRequest request) {
        return request == null
                ? null
                :Horario.crear(
                        request.dia().toString(),
                        request.horaInicio(),
                        request.horaFin());
    }
    public Horario requestAEntidad(HorarioRequest request, Grupo grupo) {
        if(request == null) return null;

        Horario horario = requestAEntidad(request);

        horario.asignarGrupo(grupo);

        return horario;
    }
    @Override
    public HorarioResponse entidadAResponse(Horario entidad) {
        return entidad == null
                ? null
                : new HorarioResponse(
                        entidad.getId(),
                        datosGrupo(entidad.getGrupo()),
                        entidad.formatoHora(
                                entidad.getDiaSemana().toString(),
                                entidad.getHoraInicio(),
                                entidad.getHoraFin()
                        ));
    }

    public DatosHorarios entidadADatos(Horario entidad){
        return entidad == null
                ? null
                : new DatosHorarios(
                        entidad.formatoHora(
                                entidad.getDiaSemana().toString(),
                                entidad.getHoraInicio(),
                                entidad.getHoraFin()));
    }

    public String entidadAString(Horario entidad){
        return entidad == null
                ? null
                : new String(
                entidad.formatoHoraParaGrupo(
                        entidad.getDiaSemana().toString(),
                        entidad.getHoraInicio(),
                        entidad.getHoraFin()));
    }

    public DatosGrupo datosGrupo(Grupo entidad){
        return entidad == null
                ? null
                : new DatosGrupo(
                entidad.getCurso().getNombre(),
                entidad.getMaestro().obtenerNombreCompleto(),
                entidad.getAula().getNombre(),
                entidad.getPeriodo());
    }

}
