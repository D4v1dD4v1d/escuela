package com.david.escuela.entities;

import com.david.escuela.utils.StringCustomUtils;
import com.david.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AULAS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_AULA")
    private Long id;

    @Column(name = "NOMBRE", nullable = false,length = 100, unique = true)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    private static void validarDatos(String nombre, Integer capacidad){
        StringCustomUtils.validarTamanio(nombre,8,100,
                "El nombre es requerido y debe tener entre 8 y 100 caracteres");

        ValoresNumericosUtils.validarEnteroPositivo(capacidad, "La capacidad debe ser un entero positivo");
    }

    public void actualizar(String nombre, Integer capacidad){
        validarDatos(nombre,capacidad);
        this.nombre=nombre;
        this.capacidad=capacidad;
    }

    public static Aula crear(String nombre,Integer capacidad){
        validarDatos(nombre,capacidad);

        return Aula.builder()
                .nombre(nombre)
                .capacidad(capacidad)
                .build();
    }

}
