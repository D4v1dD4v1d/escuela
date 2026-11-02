package com.david.escuela.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MAESTROS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Maestro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_MAESTRO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false,length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false,length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false,length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false,length = 100, unique = true)
    private String email;

    @Column(name = "TELEFONO", nullable = false,length = 10, unique = true)
    private String telefono;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "maestro")
    private List<Grupo> grupos = new ArrayList<>();
}
