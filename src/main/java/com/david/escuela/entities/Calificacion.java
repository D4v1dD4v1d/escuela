package com.david.escuela.entities;

import com.david.escuela.exceptions.DatoInvalidoException;
import com.david.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

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

    @Builder.Default
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDate fechaRegistro =LocalDate.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSCRIPCION", nullable = false, unique = true)
    private Inscripcion inscripcion;

    public static void  validarDatos(BigDecimal calificacion){
        ValoresNumericosUtils.validarBigDecimalPositivo(calificacion,"La calificación es requerida y debe ser positiva");

    }

    public void actualizar(BigDecimal calificacion,Inscripcion inscripcion){
        validarDatos(calificacion);

        this.calificacion= calificacion;
        this.inscripcion=inscripcion;
    }

    public void asignarInscripcion(Inscripcion inscripcion){
        this.inscripcion=inscripcion;
    }



    public static Calificacion crear(BigDecimal calificacion ){
        validarDatos(calificacion);

        return Calificacion.builder()
                .calificacion(calificacion)
                .build();
    }
}
