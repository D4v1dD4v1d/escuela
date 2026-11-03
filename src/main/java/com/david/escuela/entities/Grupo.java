package com.david.escuela.entities;

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
    @JoinColumn(name = "ID_AULA")
    private Aula aula;

    @Column(name = "PERIODO", length = 20, nullable = false)
    private String periodo;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "grupo")
    private List<Horario> horarios = new ArrayList<>();

    public static void validarDatos(Curso curso, Maestro maestro, Aula aula, String periodo){

        if(periodo!=null)
        StringCustomUtils.validarTamanio(periodo,5,10,"El periodo es requerido y debe contener entre 5 y 10 caracteres.");

        if(curso==null)
            throw new DatoInvalidoException("El curso es requerido.");
        if(maestro==null)
            throw new DatoInvalidoException("El maestro es requerido.");
        if(aula==null)
            throw new DatoInvalidoException("El aula es requerido.");
    }


    public void actualizar(Curso curso, Maestro maestro, Aula aula, String periodo){
        validarDatos(curso,maestro,aula,periodo);

        this.curso= curso;
        this.maestro = maestro;
        this.aula = aula;
        this.periodo=periodo;

    }

    public static Grupo crear(Curso curso, Maestro maestro, Aula aula){
        validarDatos(curso,maestro,aula,null);

        return Grupo.builder()
                .curso(curso)
                .maestro(maestro)
                .aula(aula)
                .build();
    }

}
