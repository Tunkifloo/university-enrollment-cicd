package com.springback.apimatriculas.service.impl;

import com.springback.apimatriculas.domain.model.Facultad;
import com.springback.apimatriculas.dto.mapper.FacultadMapper;
import com.springback.apimatriculas.dto.request.FacultadRequestDTO;
import com.springback.apimatriculas.dto.response.FacultadResponseDTO;
import com.springback.apimatriculas.exception.custom.BusinessRuleException;
import com.springback.apimatriculas.exception.custom.DuplicateResourceException;
import com.springback.apimatriculas.exception.custom.ResourceNotFoundException;
import com.springback.apimatriculas.repository.FacultadRepository;
import com.springback.apimatriculas.service.EventPublisherService;
import com.springback.apimatriculas.service.interfaces.IFacultadService;
import com.springback.apimatriculas.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacultadServiceImpl implements IFacultadService {

    private final FacultadRepository facultadRepository;
    private final FacultadMapper facultadMapper;
    private final EventPublisherService eventPublisher;

    @Override
    @Transactional
    public FacultadResponseDTO create(FacultadRequestDTO requestDTO) {
        log.info("Creando nueva facultad con nombre: {}", requestDTO.nombre());

        // Validar que no exista una facultad con el mismo nombre
        if (facultadRepository.existsByNombre(requestDTO.nombre())) {
            throw new DuplicateResourceException(Constants.FACULTAD, Constants.FIELD_NOMBRE, requestDTO.nombre());
        }

        // Convertir DTO a Entidad
        Facultad facultad = facultadMapper.toEntity(requestDTO);

        // Guardar en la base de datos
        Facultad savedFacultad = facultadRepository.save(facultad);

        log.info("Facultad creada exitosamente con ID: {}", savedFacultad.getFacultadId());

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishFacultyCreatedEvent(
                savedFacultad.getFacultadId(),
                savedFacultad.getNombre()
        );

        // Convertir a DTO de respuesta
        return facultadMapper.toResponseDTO(savedFacultad);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultadResponseDTO getById(Long id) {
        log.info("Buscando facultad con ID: {}", id);

        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.FACULTAD, id));

        return facultadMapper.toResponseDTO(facultad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultadResponseDTO> getAll() {
        log.info("Obteniendo todas las facultades");

        List<Facultad> facultades = facultadRepository.findAll();

        return facultadMapper.toResponseDTOList(facultades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultadResponseDTO> getAllActive() {
        log.info("Obteniendo todas las facultades activas");

        List<Facultad> facultades = facultadRepository.findByActivoTrue();

        return facultadMapper.toResponseDTOList(facultades);
    }

    @Override
    @Transactional
    public FacultadResponseDTO update(Long id, FacultadRequestDTO requestDTO) {
        log.info("Actualizando facultad con ID: {}", id);

        // Buscar la facultad existente
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.FACULTAD, id));

        // Validar que el nombre no esté duplicado (si cambió)
        if (!facultad.getNombre().equals(requestDTO.nombre()) &&
                facultadRepository.existsByNombre(requestDTO.nombre())) {
            throw new DuplicateResourceException(Constants.FACULTAD, Constants.FIELD_NOMBRE, requestDTO.nombre());
        }

        // Actualizar los campos
        facultadMapper.updateEntityFromDTO(requestDTO, facultad);

        // Guardar cambios
        Facultad updatedFacultad = facultadRepository.save(facultad);

        log.info("Facultad actualizada exitosamente con ID: {}", updatedFacultad.getFacultadId());

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishFacultyUpdatedEvent(
                updatedFacultad.getFacultadId(),
                updatedFacultad.getNombre()
        );

        return facultadMapper.toResponseDTO(updatedFacultad);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando facultad con ID: {}", id);

        // Buscar la facultad existente
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.FACULTAD, id));

        // Verificar si tiene carreras activas asociadas
        Long carrerasActivas = facultadRepository.countCarrerasActivasByFacultadId(id);
        if (carrerasActivas > 0) {
            throw new BusinessRuleException(
                    String.format("No se puede eliminar la facultad '%s' porque tiene %d carrera(s) activa(s) asociada(s)",
                            facultad.getNombre(), carrerasActivas)
            );
        }

        // Realizar eliminación lógica
        facultad.setActivo(false);
        facultadRepository.save(facultad);

        log.info("Facultad eliminada (desactivada) exitosamente con ID: {}", id);

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishFacultyDeletedEvent(
                facultad.getFacultadId(),
                facultad.getNombre()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultadResponseDTO> searchByNombre(String nombre) {
        log.info("Buscando facultades por nombre: {}", nombre);

        List<Facultad> facultades = facultadRepository.findByNombreContaining(nombre);

        return facultadMapper.toResponseDTOList(facultades);
    }
}