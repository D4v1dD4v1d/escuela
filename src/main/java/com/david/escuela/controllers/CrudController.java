package com.david.escuela.controllers;

import com.david.escuela.docs.ProblemaDoc;
import com.david.escuela.services.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Validated
public class CrudController<RQ, RS, S extends CrudService<RQ,RS>> {

    protected final S service;

    @GetMapping
    @Operation(summary = "Listar registros")
    @ApiResponse(responseCode = "200",description = "Listado obtenido")
    public ResponseEntity<List<RS>> listar(){
        return ResponseEntity.ok(service.lista());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por id")
    @ApiResponse(responseCode = "200", description = "registro encontrada")
    @ApiResponse(responseCode = "404", description = "El recurso no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<RS>obtenerPorId(
            @Parameter(description = "Id del registro", example = "1")
            @PathVariable @Positive(message = "El id debe ser positivo")
            Long id
    ){
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo recurso")
    @ApiResponse(responseCode = "201",description = "Recurso creado.")
    @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<RS> registrar(
            @Valid @RequestBody RQ request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un recurso existente")
    @ApiResponse(responseCode = "200",description = "Recurso actualizado.")
    @ApiResponse(responseCode = "404", description = "El recurso no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<RS> actualizar(
            @Valid @RequestBody RQ request,
            @Parameter(description = "Id del recurso.", example = "1")
            @PathVariable @Positive(message = "El id debe ser positivo.") Long id

    ){
        return ResponseEntity.ok(service.actualizar(request,id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un recurso")
    @ApiResponse(responseCode = "204", description = "Recurso eliminado")
    @ApiResponse(responseCode = "404", description = "El recurso no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @ApiResponse(responseCode = "409", description = "El recurso esta en uso y no pudo eliminarse.",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity eliminar(@PathVariable Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
