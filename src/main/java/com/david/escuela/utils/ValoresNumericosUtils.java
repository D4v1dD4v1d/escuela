package com.david.escuela.utils;

import com.david.escuela.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {
    public static <N extends Number> void validarNumeroRequerido(N numero, String mensaje){
        if(numero == null)
            throw new DatoInvalidoException(mensaje);

    }

    public static  void validarEnteroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero,mensaje);
        if(numero < 0)
            throw new DatoInvalidoException(mensaje);

    }
    public static  void validarEnteroPositivoConLimite(Integer numero,Integer limite, String mensaje){
        validarNumeroRequerido(numero,mensaje);
        validarNumeroRequerido(limite,mensaje);
        if(numero <= 0 || numero > limite)
            throw new DatoInvalidoException(mensaje);

    }
    public static  void validarBigDecimalPositivo(BigDecimal numero, String mensaje){
        validarNumeroRequerido(numero,mensaje);
        if(numero.compareTo(BigDecimal.ZERO)<0)
            throw new DatoInvalidoException(mensaje);

    }



}
