package com.david.escuela.entities;

import com.david.escuela.enums.DiaSemana;
import jakarta.persistence.*;
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
}
