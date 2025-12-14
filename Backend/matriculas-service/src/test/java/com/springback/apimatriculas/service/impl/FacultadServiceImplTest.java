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
class FacultadServiceImplTest {

    @Mock
    private FacultadRepository facultadRepository;

    @Mock
    private FacultadMapper facultadMapper;

    @Mock
    private EventPublisherService eventPublisher;

    @InjectMocks
    private FacultadServiceImpl facultadService;

    private Facultad facultad;
    private FacultadRequestDTO requestDTO;
    private FacultadResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        facultad = Facultad.builder()
                .facultadId(1L)
                .nombre("Facultad de Ingeniería")
                .descripcion("Descripción test")
                .ubicacion("Pabellón A")
                .decano("Dr. Test")
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        requestDTO = new FacultadRequestDTO(
                "Facultad de Ingeniería",
                "Descripción test",
                "Pabellón A",
                "Dr. Test",
                true
        );

        responseDTO = new FacultadResponseDTO(
                1L,
                "Facultad de Ingeniería",
                "Descripción test",
                "Pabellón A",
                "Dr. Test",
                LocalDateTime.now(),
                true,
                0L
        );
    }

    @Test
    void create_Success() {
        when(facultadRepository.existsByNombre(anyString())).thenReturn(false);
        when(facultadMapper.toEntity(any(FacultadRequestDTO.class))).thenReturn(facultad);
        when(facultadRepository.save(any(Facultad.class))).thenReturn(facultad);
        when(facultadMapper.toResponseDTO(any(Facultad.class))).thenReturn(responseDTO);

        FacultadResponseDTO result = facultadService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Facultad de Ingeniería");
        verify(facultadRepository).existsByNombre("Facultad de Ingeniería");
        verify(facultadRepository).save(any(Facultad.class));
        verify(eventPublisher).publishFacultyCreatedEvent(anyLong(), anyString());
    }

    @Test
    void create_DuplicateName_ThrowsException() {
        when(facultadRepository.existsByNombre(anyString())).thenReturn(true);

        assertThatThrownBy(() -> facultadService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(facultadRepository, never()).save(any(Facultad.class));
    }

    @Test
    void getById_Success() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(facultadMapper.toResponseDTO(any(Facultad.class))).thenReturn(responseDTO);

        FacultadResponseDTO result = facultadService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.facultadId()).isEqualTo(1L);
        verify(facultadRepository).findById(1L);
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultadService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_ReturnsAllFacultades() {
        List<Facultad> facultades = Arrays.asList(facultad);
        List<FacultadResponseDTO> responses = Arrays.asList(responseDTO);

        when(facultadRepository.findAll()).thenReturn(facultades);
        when(facultadMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<FacultadResponseDTO> result = facultadService.getAll();

        assertThat(result).hasSize(1);
        verify(facultadRepository).findAll();
    }

    @Test
    void getAllActive_ReturnsActiveFacultades() {
        List<Facultad> facultades = Arrays.asList(facultad);
        List<FacultadResponseDTO> responses = Arrays.asList(responseDTO);

        when(facultadRepository.findByActivoTrue()).thenReturn(facultades);
        when(facultadMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<FacultadResponseDTO> result = facultadService.getAllActive();

        assertThat(result).hasSize(1);
        verify(facultadRepository).findByActivoTrue();
    }

    @Test
    void update_Success() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(facultadRepository.save(any(Facultad.class))).thenReturn(facultad);
        when(facultadMapper.toResponseDTO(any(Facultad.class))).thenReturn(responseDTO);

        FacultadResponseDTO result = facultadService.update(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(facultadMapper).updateEntityFromDTO(any(FacultadRequestDTO.class), any(Facultad.class));
        verify(facultadRepository).save(any(Facultad.class));
        verify(eventPublisher).publishFacultyUpdatedEvent(anyLong(), anyString());
    }

    @Test
    void update_NotFound_ThrowsException() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultadService.update(1L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(facultadRepository, never()).save(any(Facultad.class));
    }

    @Test
    void delete_Success() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(facultadRepository.countCarrerasActivasByFacultadId(1L)).thenReturn(0L);
        when(facultadRepository.save(any(Facultad.class))).thenReturn(facultad);

        facultadService.delete(1L);

        verify(facultadRepository).save(any(Facultad.class));
        verify(eventPublisher).publishFacultyDeletedEvent(anyLong(), anyString());
    }

    @Test
    void delete_WithActiveCarreras_ThrowsException() {
        when(facultadRepository.findById(1L)).thenReturn(Optional.of(facultad));
        when(facultadRepository.countCarrerasActivasByFacultadId(1L)).thenReturn(3L);

        assertThatThrownBy(() -> facultadService.delete(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("tiene 3 carrera(s) activa(s)");

        verify(facultadRepository, never()).save(any(Facultad.class));
    }

    @Test
    void searchByNombre_ReturnsMatchingFacultades() {
        List<Facultad> facultades = Arrays.asList(facultad);
        List<FacultadResponseDTO> responses = Arrays.asList(responseDTO);

        when(facultadRepository.findByNombreContaining("Ingeniería")).thenReturn(facultades);
        when(facultadMapper.toResponseDTOList(anyList())).thenReturn(responses);

        List<FacultadResponseDTO> result = facultadService.searchByNombre("Ingeniería");

        assertThat(result).hasSize(1);
        verify(facultadRepository).findByNombreContaining("Ingeniería");
    }
}