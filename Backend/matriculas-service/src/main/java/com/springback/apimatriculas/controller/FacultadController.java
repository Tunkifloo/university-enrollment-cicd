package com.springback.apimatriculas.controller;

import com.springback.apimatriculas.dto.request.FacultadRequestDTO;
import com.springback.apimatriculas.dto.response.FacultadResponseDTO;
import com.springback.apimatriculas.exception.ErrorResponse;
import com.springback.apimatriculas.service.interfaces.IFacultadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/facultades")
@RequiredArgsConstructor
@Tag(name = "Facultades", description = "API para la gestión de Facultades")
public class FacultadController {

    private final IFacultadService facultadService;

    @Operation(summary = "Crear una nueva facultad", description = "Crea una nueva facultad en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facultad creada exitosamente",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La facultad ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<FacultadResponseDTO> create(
            @Valid @RequestBody FacultadRequestDTO requestDTO) {
        log.info("POST /facultades - Creando nueva facultad");
        FacultadResponseDTO response = facultadService.create(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener facultad por ID", description = "Obtiene los detalles de una facultad específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facultad encontrada",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacultadResponseDTO> getById(
            @Parameter(description = "ID de la facultad", example = "1")
            @PathVariable Long id) {
        log.info("GET /facultades/{} - Obteniendo facultad por ID", id);
        FacultadResponseDTO response = facultadService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las facultades", description = "Obtiene una lista de todas las facultades registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facultades obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<FacultadResponseDTO>> getAll() {
        log.info("GET /facultades - Obteniendo todas las facultades");
        List<FacultadResponseDTO> response = facultadService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar facultades activas", description = "Obtiene una lista de todas las facultades activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facultades activas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class)))
    })
    @GetMapping("/activas")
    public ResponseEntity<List<FacultadResponseDTO>> getAllActive() {
        log.info("GET /facultades/activas - Obteniendo facultades activas");
        List<FacultadResponseDTO> response = facultadService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar facultades por nombre", description = "Busca facultades que contengan el texto especificado en su nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class)))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<FacultadResponseDTO>> searchByNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "Ingeniería")
            @RequestParam String nombre) {
        log.info("GET /facultades/buscar?nombre={} - Buscando facultades por nombre", nombre);
        List<FacultadResponseDTO> response = facultadService.searchByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar una facultad", description = "Actualiza los datos de una facultad existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facultad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = FacultadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El nombre de la facultad ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<FacultadResponseDTO> update(
            @Parameter(description = "ID de la facultad", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody FacultadRequestDTO requestDTO) {
        log.info("PUT /facultades/{} - Actualizando facultad", id);
        FacultadResponseDTO response = facultadService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una facultad", description = "Realiza una eliminación lógica de la facultad (la desactiva)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Facultad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar la facultad porque tiene carreras activas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la facultad", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /facultades/{} - Eliminando facultad", id);
        facultadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
