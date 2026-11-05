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
    private String horaInicio;

    @Column(name = "HORA_FIN", nullable = false,length = 5)
    private String horaFin;

    public static void validarDatos(String diaSemana, String horaIinicio, String horaFin){
        StringCustomUtils.validarNoVacio(horaIinicio,"La hora de inicio es requerida");
        StringCustomUtils.validarNoVacio(horaFin,"La hora de fin es requerida");
        DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana);
    }

    public void actualizar(Grupo grupo, String diaSemana,String horaInicio, String horaFin){
        validarDatos(diaSemana,horaInicio,horaFin);

        this.grupo=grupo;
        this.diaSemana=DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana);
        this.horaInicio=horaInicio;
        this.horaFin=horaFin;
    }
    public String formatoHora(String dia,String inicio,String fin){
        DiaSemana diaSemana = DiaSemana.obteterDiaSemanaPorDescripcion(dia);
        return diaSemana.getDescipcion() + " " + inicio+ " " + fin;
    }
    public String formatoHoraParaGrupo(String dia,String inicio,String fin){
        DiaSemana diaSemana = DiaSemana.obteterDiaSemanaPorDescripcion(dia);
        return diaSemana.getDescipcion() + " " + inicio+ " - " + fin;
    }
    public void asignarGrupo(Grupo grupo){
        if (grupo != null)
            this.grupo=grupo;
    }
    public static Horario crear(String diaSemana, String horaIinicio, String horaFin){
        validarDatos(diaSemana,horaIinicio,horaFin);

        return Horario.builder()
                .diaSemana(DiaSemana.obteterDiaSemanaPorDescripcion(diaSemana))
                .horaInicio(horaIinicio)
                .horaFin(horaFin)
                .build();
    }
}
