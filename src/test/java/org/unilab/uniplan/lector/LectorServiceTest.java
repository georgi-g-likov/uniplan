package org.unilab.uniplan.lector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.unilab.uniplan.lector.dto.LectorDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectorServiceTest {

    @Mock
    private LectorRepository lectorRepository;

    @Mock
    private LectorMapper lectorMapper;

    @InjectMocks
    private LectorService lectorService;

    private UUID id;
    private UUID facultyId;
    private String fName;
    private String lName;
    private String email;
    private LectorDto lectorDto;
    private Lector lector;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        facultyId = UUID.randomUUID();
        fName = "Ivan";
        lName = "Ivanov";
        email = "i.ivanov@gmail.com";
        lectorDto = new LectorDto(id, facultyId, email, fName, lName);
        lector = new Lector();
    }

    @Test
    void testCreateLectorShouldSaveAndReturnDto() {
        when(lectorMapper.toEntity(lectorDto)).thenReturn(lector);
        when(lectorRepository.save(lector)).thenReturn(lector);
        when(lectorMapper.toDto(lector)).thenReturn(lectorDto);

        LectorDto result = lectorService.createLector(lectorDto);

        assertEquals(lectorDto, result);
    }

    @Test
    void testGetAllLectorsShouldReturnListOfLectorDtos() {
        List<Lector> lectors = List.of(lector);
        List<LectorDto> lectorDtos = List.of(lectorDto);

        when(lectorRepository.findAll()).thenReturn(lectors);
        when(lectorMapper.toDtos(lectors)).thenReturn(lectorDtos);

        List<LectorDto> result = lectorService.getAllLectors();

        assertEquals(lectorDtos, result);
    }

    @Test
    void testGetLectorByIdShouldReturnLectorIfFound() {
        when(lectorRepository.findById(id)).thenReturn(Optional.of(lector));
        when(lectorMapper.toDto(lector)).thenReturn(lectorDto);

        Optional<LectorDto> result = lectorService.getLectorById(id);

        assertTrue(result.isPresent());
        assertEquals(lectorDto, result.get());
    }

    @Test
    void testGetLectorByIdShouldReturnEmptyOptional() {
        when(lectorRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LectorDto> result = lectorService.getLectorById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateLectorShouldUpdateAndReturnDtoIfFound() {
        when(lectorRepository.findById(id)).thenReturn(Optional.of(lector));
        doAnswer(invocation -> null).when(lectorMapper).updateEntityFromDto(lectorDto, lector);
        when(lectorRepository.save(lector)).thenReturn(lector);
        when(lectorMapper.toDto(lector)).thenReturn(lectorDto);

        Optional<LectorDto> result = lectorService.updateLector(id, lectorDto);

        assertTrue(result.isPresent());
        assertEquals(lectorDto, result.get());
    }

    @Test
    void testUpdateLectorShouldReturnEmptyOptionalIfNotFound() {
        when(lectorRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LectorDto> result = lectorService.updateLector(id, lectorDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteLectorShouldDeleteLectorIfFound() {
        when(lectorRepository.findById(id)).thenReturn(Optional.of(lector));
        doNothing().when(lectorRepository).delete(lector);

        assertDoesNotThrow(() -> lectorService.deleteLector(id));
        verify(lectorRepository).delete(lector);
    }

    @Test
    void testDeleteLectorShouldThrowIfNotFound() {
        when(lectorRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> lectorService.deleteLector(id));

        assertTrue(exception.getMessage().contains(id.toString()));
    }
}