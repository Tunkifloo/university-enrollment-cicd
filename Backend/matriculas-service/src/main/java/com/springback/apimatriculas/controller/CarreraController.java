package com.springback.apimatriculas.controller;

import com.springback.apimatriculas.dto.request.CarreraRequestDTO;
import com.springback.apimatriculas.dto.response.CarreraResponseDTO;
import com.springback.apimatriculas.exception.ErrorResponse;
import com.springback.apimatriculas.service.interfaces.ICarreraService;
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
@RequestMapping("/carreras")
@RequiredArgsConstructor
@Tag(name = "Carreras", description = "API para la gestión de Carreras")
public class CarreraController {

    private final ICarreraService carreraService;

    @Operation(summary = "Crear una nueva carrera", description = "Crea una nueva carrera en el sistema asociada a una facultad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrera creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o facultad inactiva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La carrera ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CarreraResponseDTO> create(
            @Valid @RequestBody CarreraRequestDTO requestDTO) {
        log.info("POST /carreras - Creando nueva carrera");
        CarreraResponseDTO response = carreraService.create(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener carrera por ID", description = "Obtiene los detalles de una carrera específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrera encontrada",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Carrera no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> getById(
            @Parameter(description = "ID de la carrera", example = "1")
            @PathVariable Long id) {
        log.info("GET /carreras/{} - Obteniendo carrera por ID", id);
        CarreraResponseDTO response = carreraService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las carreras", description = "Obtiene una lista de todas las carreras registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carreras obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> getAll() {
        log.info("GET /carreras - Obteniendo todas las carreras");
        List<CarreraResponseDTO> response = carreraService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar carreras activas", description = "Obtiene una lista de todas las carreras activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carreras activas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class)))
    })
    @GetMapping("/activas")
    public ResponseEntity<List<CarreraResponseDTO>> getAllActive() {
        log.info("GET /carreras/activas - Obteniendo carreras activas");
        List<CarreraResponseDTO> response = carreraService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar carreras por facultad", description = "Obtiene todas las carreras asociadas a una facultad específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carreras de la facultad obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/facultad/{facultadId}")
    public ResponseEntity<List<CarreraResponseDTO>> getByFacultadId(
            @Parameter(description = "ID de la facultad", example = "1")
            @PathVariable Long facultadId) {
        log.info("GET /carreras/facultad/{} - Obteniendo carreras por facultad", facultadId);
        List<CarreraResponseDTO> response = carreraService.getByFacultadId(facultadId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar carreras por nombre", description = "Busca carreras que contengan el texto especificado en su nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class)))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<CarreraResponseDTO>> searchByNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "Sistemas")
            @RequestParam String nombre) {
        log.info("GET /carreras/buscar?nombre={} - Buscando carreras por nombre", nombre);
        List<CarreraResponseDTO> response = carreraService.searchByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar una carrera", description = "Actualiza los datos de una carrera existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrera actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CarreraResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o facultad inactiva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carrera o facultad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El nombre de la carrera ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CarreraResponseDTO> update(
            @Parameter(description = "ID de la carrera", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CarreraRequestDTO requestDTO) {
        log.info("PUT /carreras/{} - Actualizando carrera", id);
        CarreraResponseDTO response = carreraService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una carrera", description = "Realiza una eliminación lógica de la carrera (la desactiva)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrera eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carrera no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la carrera", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /carreras/{} - Eliminando carrera", id);
        carreraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
