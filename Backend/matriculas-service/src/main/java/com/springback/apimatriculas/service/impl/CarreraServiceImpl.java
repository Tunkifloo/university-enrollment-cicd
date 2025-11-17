package com.springback.apimatriculas.service.impl;

import com.springback.apimatriculas.domain.model.Carrera;
import com.springback.apimatriculas.domain.model.Facultad;
import com.springback.apimatriculas.dto.mapper.CarreraMapper;
import com.springback.apimatriculas.dto.request.CarreraRequestDTO;
import com.springback.apimatriculas.dto.response.CarreraResponseDTO;
import com.springback.apimatriculas.exception.custom.BusinessRuleException;
import com.springback.apimatriculas.exception.custom.DuplicateResourceException;
import com.springback.apimatriculas.exception.custom.ResourceNotFoundException;
import com.springback.apimatriculas.repository.CarreraRepository;
import com.springback.apimatriculas.repository.FacultadRepository;
import com.springback.apimatriculas.service.EventPublisherService;
import com.springback.apimatriculas.service.interfaces.ICarreraService;
import com.springback.apimatriculas.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarreraServiceImpl implements ICarreraService {

    private final CarreraRepository carreraRepository;
    private final FacultadRepository facultadRepository;
    private final CarreraMapper carreraMapper;
    private final EventPublisherService eventPublisher;

    @Override
    @Transactional
    public CarreraResponseDTO create(CarreraRequestDTO requestDTO) {
        log.info("Creando nueva carrera con nombre: {}", requestDTO.nombre());

        // Validar que la facultad exista y esté activa
        Facultad facultad = facultadRepository.findById(requestDTO.facultadId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.FACULTAD, requestDTO.facultadId()));

        if (!facultad.getActivo()) {
            throw new BusinessRuleException("No se puede crear una carrera en una facultad inactiva");
        }

        // Validar que no exista una carrera con el mismo nombre
        if (carreraRepository.existsByNombre(requestDTO.nombre())) {
            throw new DuplicateResourceException(Constants.CARRERA, Constants.FIELD_NOMBRE, requestDTO.nombre());
        }

        // Convertir DTO a Entidad
        Carrera carrera = carreraMapper.toEntity(requestDTO);
        carrera.setFacultad(facultad);

        // Guardar en la base de datos
        Carrera savedCarrera = carreraRepository.save(carrera);

        log.info("Carrera creada exitosamente con ID: {}", savedCarrera.getCarreraId());

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishCareerCreatedEvent(
                savedCarrera.getCarreraId(),
                savedCarrera.getNombre(),
                facultad.getFacultadId(),
                facultad.getNombre()
        );

        return carreraMapper.toResponseDTO(savedCarrera);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO getById(Long id) {
        log.info("Buscando carrera con ID: {}", id);

        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARRERA, id));

        return carreraMapper.toResponseDTO(carrera);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> getAll() {
        log.info("Obteniendo todas las carreras");

        List<Carrera> carreras = carreraRepository.findAll();

        return carreraMapper.toResponseDTOList(carreras);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> getAllActive() {
        log.info("Obteniendo todas las carreras activas");

        List<Carrera> carreras = carreraRepository.findByActivoTrue();

        return carreraMapper.toResponseDTOList(carreras);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> getByFacultadId(Long facultadId) {
        log.info("Obteniendo carreras de la facultad con ID: {}", facultadId);

        // Validar que la facultad exista
        if (!facultadRepository.existsById(facultadId)) {
            throw new ResourceNotFoundException(Constants.FACULTAD, facultadId);
        }

        List<Carrera> carreras = carreraRepository.findByFacultad_FacultadId(facultadId);

        return carreraMapper.toResponseDTOList(carreras);
    }

    @Override
    @Transactional
    public CarreraResponseDTO update(Long id, CarreraRequestDTO requestDTO) {
        log.info("Actualizando carrera con ID: {}", id);

        // Buscar la carrera existente
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARRERA, id));

        // Validar que la nueva facultad exista y esté activa (si cambió)
        Facultad facultad = carrera.getFacultad();
        if (!carrera.getFacultad().getFacultadId().equals(requestDTO.facultadId())) {
            Facultad nuevaFacultad = facultadRepository.findById(requestDTO.facultadId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.FACULTAD, requestDTO.facultadId()));

            if (!nuevaFacultad.getActivo()) {
                throw new BusinessRuleException("No se puede asignar una carrera a una facultad inactiva");
            }

            facultad = nuevaFacultad;
            carrera.setFacultad(nuevaFacultad);
        }

        // Validar que el nombre no esté duplicado (si cambió)
        if (!carrera.getNombre().equals(requestDTO.nombre()) &&
                carreraRepository.existsByNombre(requestDTO.nombre())) {
            throw new DuplicateResourceException(Constants.CARRERA, Constants.FIELD_NOMBRE, requestDTO.nombre());
        }

        // Actualizar los campos
        carreraMapper.updateEntityFromDTO(requestDTO, carrera);

        // Guardar cambios
        Carrera updatedCarrera = carreraRepository.save(carrera);

        log.info("Carrera actualizada exitosamente con ID: {}", updatedCarrera.getCarreraId());

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishCareerUpdatedEvent(
                updatedCarrera.getCarreraId(),
                updatedCarrera.getNombre(),
                facultad.getFacultadId(),
                facultad.getNombre()
        );

        return carreraMapper.toResponseDTO(updatedCarrera);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando carrera con ID: {}", id);

        // Buscar la carrera existente
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARRERA, id));

        // Realizar eliminación lógica
        carrera.setActivo(false);
        carreraRepository.save(carrera);

        log.info("Carrera eliminada (desactivada) exitosamente con ID: {}", id);

        // PUBLICAR EVENTO EN KAFKA
        eventPublisher.publishCareerDeletedEvent(
                carrera.getCarreraId(),
                carrera.getNombre()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> searchByNombre(String nombre) {
        log.info("Buscando carreras por nombre: {}", nombre);

        List<Carrera> carreras = carreraRepository.findByNombreContaining(nombre);

        return carreraMapper.toResponseDTOList(carreras);
    }
}
