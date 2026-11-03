package com.david.escuela.mappers;

import com.david.escuela.dto.cursos.CursoRequest;
import com.david.escuela.dto.cursos.CursoResponse;
import com.david.escuela.dto.datos.DatosCurso;
import com.david.escuela.entities.Curso;
import org.springframework.stereotype.Component;

@Component
public class CursoMapper implements CommonMapper <CursoRequest, Curso, CursoResponse>{

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return null;
    }

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        return null;
    }

    public DatosCurso entidadADatos(Curso entidad){
        return entidad == null
                ? null
                : new DatosCurso(
                        entidad.getNombre(),
                        entidad.getDescripcion(),
                        entidad.getCreditos()
        );
    }
}
