package com.david.escuela.entities;

import com.david.escuela.utils.StringCustomUtils;
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

    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono){
        StringCustomUtils.validarTamanio(nombre,1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno,1,50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email,8,100,
                "El email es requerido y debe tener entre 8 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono,1,50,
                "El telefono es requerido y debe tener 10 caracteres");
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono){
        validarDatos(nombre,apellidoPaterno,apellidoMaterno,email,telefono);

        this.nombre = nombre.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.telefono=telefono.trim();
        this.email=email.toLowerCase().trim();
    }

    public static Maestro crear(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono){
        validarDatos(nombre,apellidoPaterno,apellidoMaterno,email,telefono);

        return Maestro.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .build();

    }

}
