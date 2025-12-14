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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarreraServiceImplTest {

    @Mock
    private CarreraRepository carreraRepository;

    @Mock
    private FacultadRepository facultadRepository;

    @Mock
    private CarreraMapper carreraMapper;

    @Mock
    private EventPublisherService eventPublisher;

    @InjectMocks
    private CarreraServiceImpl carreraService;

    private Carrera carrera;
    private Facultad facultad;
    private CarreraRequestDTO requestDTO;
    private CarreraResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        facultad = Facultad.builder()
                .facultadId(1L)
                .nombre("Facultad de Ingeniería")
                .activo(true)
                .build();

        carrera = Carrera.builder()
                .carreraId(1L)
                .nombre("Ingeniería de Sistemas")
                .descripcion("Descripción test")
                .duracionSemestres(10)
                .tituloOtorgado("Ingeniero de Sistemas")
                .activo(true)
                .facultad(facultad)
                .fechaRegistro(LocalDateTime.now())
                .build();

        requestDTO = new CarreraRequestDTO(
                1L,
                "Ingeniería de Sistemas",
                "Descripción test",
                10,
                "Ingeniero de Sistemas",
                true
        );

        responseDTO = new CarreraResponseDTO(
                1L,
                1L,
                "Facultad de Ingeniería",
                "Ingeniería de Sistemas",
                "Descripción test",
                10,
                "Ingeniero de Sistemas",
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void create_Success() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(carreraRepository.existsByNombre(anyString())).thenReturn(false);
        when(carreraMapper.toEntity(any(CarreraRequestDTO.class))).thenReturn(carrera);
        when(carreraRepository.save(any(Carrera.class))).thenReturn(carrera);
        when(carreraMapper.toResponseDTO(any(Carrera.class))).thenReturn(responseDTO);

        CarreraResponseDTO result = carreraService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Ingeniería de Sistemas");
        verify(facultadRepository).findById(1L);
        verify(carreraRepository).save(any(Carrera.class));
        verify(eventPublisher).publishCareerCreatedEvent(anyLong(), anyString(), anyLong(), anyString());
    }

    @Test
    void create_FacultadNotFound_ThrowsException() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carreraService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void create_InactiveFacultad_ThrowsException() {
        facultad.setActivo(false);
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));

        assertThatThrownBy(() -> carreraService.create(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("facultad inactiva");

        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void create_DuplicateName_ThrowsException() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(carreraRepository.existsByNombre(anyString())).thenReturn(true);

        assertThatThrownBy(() -> carreraService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(carreraRepository, never()).save(any(Carrera.class));
    }

    @Test
    void getById_Success() {
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(carreraMapper.toResponseDTO(any(Carrera.class))).thenReturn(responseDTO);

        CarreraResponseDTO result = carreraService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.carreraId()).isEqualTo(1L);
        verify(carreraRepository).findById(1L);
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(carreraRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carreraService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_ReturnsAllCarreras() {
        List<Carrera> carreras = Arrays.asList(carrera);
        List<CarreraResponseDTO> responses = Arrays.asList(responseDTO);

        when(carreraRepository.findAll()).thenReturn(carreras);
        when(carreraMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<CarreraResponseDTO> result = carreraService.getAll();

        assertThat(result).hasSize(1);
        verify(carreraRepository).findAll();
    }

    @Test
    void getAllActive_ReturnsActiveCarreras() {
        List<Carrera> carreras = Arrays.asList(carrera);
        List<CarreraResponseDTO> responses = Arrays.asList(responseDTO);

        when(carreraRepository.findByActivoTrue()).thenReturn(carreras);
        when(carreraMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<CarreraResponseDTO> result = carreraService.getAllActive();

        assertThat(result).hasSize(1);
        verify(carreraRepository).findByActivoTrue();
    }

    @Test
    void getByFacultadId_Success() {
        List<Carrera> carreras = Arrays.asList(carrera);
        List<CarreraResponseDTO> responses = Arrays.asList(responseDTO);

        when(facultadRepository.existsById(1L)).thenReturn(true);
        when(carreraRepository.findByFacultad_FacultadId(1L)).thenReturn(carreras);
        when(carreraMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<CarreraResponseDTO> result = carreraService.getByFacultadId(1L);

        assertThat(result).hasSize(1);
        verify(carreraRepository).findByFacultad_FacultadId(1L);
    }

    @Test
    void getByFacultadId_FacultadNotFound_ThrowsException() {
        when(facultadRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> carreraService.getByFacultadId(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(carreraRepository, never()).findByFacultad_FacultadId(anyLong());
    }

    @Test
    void update_Success() {
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(carreraRepository.save(any(Carrera.class))).thenReturn(carrera);
        when(carreraMapper.toResponseDTO(any(Carrera.class))).thenReturn(responseDTO);

        CarreraResponseDTO result = carreraService.update(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(carreraMapper).updateEntityFromDTO(any(CarreraRequestDTO.class), any(Carrera.class));
        verify(carreraRepository).save(any(Carrera.class));
        verify(eventPublisher).publishCareerUpdatedEvent(anyLong(), anyString(), anyLong(), anyString());
    }

    @Test
    void delete_Success() {
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(carreraRepository.save(any(Carrera.class))).thenReturn(carrera);

        carreraService.delete(1L);

        verify(carreraRepository).save(any(Carrera.class));
        verify(eventPublisher).publishCareerDeletedEvent(anyLong(), anyString());
    }

    @Test
    void searchByNombre_ReturnsMatchingCarreras() {
        List<Carrera> carreras = Arrays.asList(carrera);
        List<CarreraResponseDTO> responses = Arrays.asList(responseDTO);

        when(carreraRepository.findByNombreContaining("Sistemas")).thenReturn(carreras);
        when(carreraMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<CarreraResponseDTO> result = carreraService.searchByNombre("Sistemas");

        assertThat(result).hasSize(1);
        verify(carreraRepository).findByNombreContaining("Sistemas");
    }
}