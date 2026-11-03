package com.david.escuela.entities;

import com.david.escuela.utils.StringCustomUtils;
import com.david.escuela.utils.ValoresNumericosUtils;
import com.sun.source.doctree.SeeTree;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CURSOS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_CURSO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false,length = 100, unique = true)
    private String nombre;

    @Column(name = "DESCRIPCION",length = 200)
    private String descripcion;

    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;

    public static void validarDatos(String nombre, Integer creditos ){
        StringCustomUtils.validarTamanio(nombre,10,100,"El nombre del curso debe estar entre 10 y 100 caracteres y es requerido.");
        ValoresNumericosUtils.validarEnteroPositivo(creditos,"Debe tener al menos un crédito");
    }

    public void actualizar(String nombre, String descripcion,Integer creditos){
        validarDatos(nombre,creditos);

        this.nombre=nombre;
        this.descripcion=descripcion;
        this.creditos=creditos;
    }

    public static Curso crear(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre,creditos);

        return Curso.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .creditos(creditos)
                .build();
    }
}
