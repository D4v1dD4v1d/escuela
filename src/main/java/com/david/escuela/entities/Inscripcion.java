package com.david.escuela.entities;

import com.david.escuela.exceptions.DatoInvalidoException;
import com.david.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INSCRIPCIONES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_INSCRIPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALUMNO", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Builder.Default
    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion = LocalDate.now();

    @OneToOne(mappedBy = "inscripcion")
    private Calificacion calificacion;


    public static void validarDatos(Alumno alumno,Grupo grupo){
        if(alumno==null)
            throw new DatoInvalidoException("El alumno es requerido.");
        if(grupo==null)
            throw new DatoInvalidoException("El grupo es requerido.");
    }

    public void actualizar(Alumno alumno, Grupo grupo){
        validarDatos(alumno,grupo);

        this.alumno = alumno;
        this.grupo = grupo;
    }

    public void asignarAlumnoGrupo(Alumno alumno, Grupo grupo){
        this.alumno = alumno;
        this.grupo = grupo;
    }


    public static Inscripcion crear(){

        return Inscripcion.builder()
                .build();

    }


}
