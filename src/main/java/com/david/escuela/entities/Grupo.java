package com.david.escuela.entities;

import com.david.escuela.exceptions.ConflictoException;
import com.david.escuela.exceptions.DatoInvalidoException;
import com.david.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GRUPOS", uniqueConstraints = @UniqueConstraint(
        name = "GRUPO_CU_MA_AU_PE_UK",
        columnNames = {"ID_CURSO","ID_MAESTRO","ID_AULA","PERIODO"}
))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_GRUPO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAESTRO", nullable = false)
    private Maestro maestro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AULA", nullable = false)
    private Aula aula;

    @Column(name = "PERIODO", length = 20, nullable = false)
    private String periodo;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "grupo")
    private List<Horario> horarios = new ArrayList<>();

    public static void validarDatos(Curso curso, Maestro maestro, Aula aula){
        if(curso==null)
            throw new DatoInvalidoException("El curso es requerido.");
        if(maestro==null)
            throw new DatoInvalidoException("El maestro es requerido.");
        if(aula==null)
            throw new DatoInvalidoException("El aula es requerido.");
    }

    public static void validarPeriodo(String periodo){
        StringCustomUtils.validarTamanio(periodo,1,20,"El periodo es requerido" +
                "y debe contener entre 1 y 20 caracteres");
    }

    public void asignarObjetos(Maestro maestro,Curso curso ,Aula aula){
        validarDatos(curso,maestro,aula);
        this.maestro=maestro;
        this.curso=curso;
        this.aula=aula;
    }

    public void actualizar(Curso curso, Maestro maestro, Aula aula, String periodo){
        validarDatos(curso,maestro,aula);
        validarPeriodo(periodo);

        asignarObjetos(maestro,curso,aula);

        this.periodo=periodo;

    }

    public static Grupo crear(String periodo){
        validarPeriodo(periodo);
        return Grupo.builder()
                .periodo(periodo)
                .build();
    }

}
