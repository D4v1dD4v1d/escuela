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
@Table(name = "ALUMNOS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//especifica la forma en que se crea el id
    @Column(name = "ID_ALUMNO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false,length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false,length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false,length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false,length = 100,unique = true)
    private String email;

    @Column(name = "MATRICULA", nullable = false,length = 10,unique = true)
    private String matricula;

    @Builder.Default
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso = LocalDate.now();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alumno")
    private List<Inscripcion> inscripciones = new ArrayList<>();

    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email){
        StringCustomUtils.validarTamanio(nombre,1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno,1,50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        if(email!=null)
            StringCustomUtils.validarTamanio(email,8,100,
                    "El email debebe tener entre 8 y 100 caracteres");
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email){
        validarDatos(nombre,apellidoPaterno,apellidoMaterno,email);

        this.nombre = nombre.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.email=email.toLowerCase().trim();

    }

    public void inscribir(Inscripcion inscripcion){
        if (inscripcion== null)
            throw new DatoInvalidoException("La inscripción es requerida.");

        this.inscripciones.add(inscripcion);

        inscripcion.inscribirAlumno(this);
    }

    public static Alumno crear(String nombre, String apellidoPaterno, String apellidoMaterno){
        validarDatos(nombre,apellidoPaterno,apellidoMaterno,null);

        return Alumno.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .build();

    }
}
