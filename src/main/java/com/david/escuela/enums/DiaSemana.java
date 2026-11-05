package com.david.escuela.enums;

import com.david.escuela.exceptions.RecursoNoEncontradoException;
import com.david.escuela.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DiaSemana {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado");

    private final String descipcion;

    public static DiaSemana obteterDiaSemanaPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");

        String descripcionNormalizada = StringCustomUtils.normalizarTexto(descripcion);

        for (DiaSemana diaSemana :values()){
            if(StringCustomUtils.normalizarTexto(diaSemana.descipcion).equalsIgnoreCase(descripcionNormalizada))
                return diaSemana;
        }
        throw new RecursoNoEncontradoException("No existe un día de la semana con la descripción: "+ descripcion);
    }
}
