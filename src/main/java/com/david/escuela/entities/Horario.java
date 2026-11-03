package com.david.escuela.entities;

import ch.qos.logback.core.util.StringUtil;
import com.david.escuela.enums.DiaSemana;
import com.david.escuela.exceptions.DatoInvalidoException;
import com.david.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HORARIOS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_HORARIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIA", nullable = false,length = 15)
    private DiaSemana diaSemana;

    @Column(name = "HORA_INICIO", nullable = false,length = 5)
    private String horaIinicio;

    @Column(name = "HORA_FIN", nullable = false,length = 5)
    private String horaFin;

    public static void validarDatos(Grupo grupo, String diaSemana, String horaIinicio, String horaFin){
        StringCustomUtils.validarNoVacio(horaIinicio,"La hora de inicio es requerida");
        StringCustomUtils.validarNoVacio(horaFin,"La hora de fin es requerida");
        DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana);
        if (grupo==null)
            throw new DatoInvalidoException("El grupo es requerido.");
    }

    public void actualizar(Grupo grupo, String diaSemana,String horaInicio, String horaFin){
        validarDatos(grupo,diaSemana,horaInicio,horaFin);

        this.grupo=grupo;
        this.diaSemana=DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana);
        this.horaIinicio=horaInicio;
        this.horaFin=horaFin;
    }

    public static Horario crear(Grupo grupo, String diaSemana, String horaIinicio, String horaFin){
        validarDatos(grupo,diaSemana,horaIinicio,horaFin);

        return Horario.builder()
                .grupo(grupo)
                .diaSemana(DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana))
                .horaIinicio(horaIinicio)
                .horaFin(horaFin)
                .build();
    }
}
