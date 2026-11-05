package com.david.escuela.dto.maestros;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar a un maestro")
public record MaestroRequest(

        @Schema(description = "Nombre del maestro", example = "Martín")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1,max = 50,message = "El nombre debe contener entre 1 y 50 carateres")
        String nombre,

        @Schema(description = "Apellido paterno del maestro", example = "Pérez")
        @NotBlank(message = "El Apellido paterno es requerido")
        @Size(min = 1,max = 50,message = "El Apellido paterno debe contener entre 1 y 50 carateres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del maestro", example = "Zavala")
        @NotBlank(message = "El Apellido materno es requerido")
        @Size(min = 1,max = 50,message = "El Apellido materno debe contener entre 1 y 50 carateres")
        String apellidoMaterno,

        @Schema(description = "Email del maestro", example = "test@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 8,max = 100,message = "El email debe contener entre 8 y 100 carateres")
        @Email(message = "El email debe tener el formato válido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "Teléfono del maestro", example = "1234567890")
        @NotBlank(message = "El télefono es requerido")
        @Pattern(regexp = "^[0-9]{10}",message = "El télefono debe tener exactamente 10 dígitos númericos")
        String telefono
) {}
