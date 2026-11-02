package com.david.escuela.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CALIFICACIONES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_CALIFICACION")
    private Long id;

    @Column(name = "CALIFICACION", nullable = false)
    private BigDecimal calificacion;

    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDate fechaRegistro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSCRIPCION", nullable = false, unique = true)
    private Inscripcion inscripcion;

}
