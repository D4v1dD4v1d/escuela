package com.david.escuela.entities;

import jakarta.persistence.*;
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

}
