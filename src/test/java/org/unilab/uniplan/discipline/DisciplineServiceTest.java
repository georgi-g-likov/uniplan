package org.unilab.uniplan.discipline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.unilab.uniplan.discipline.dto.DisciplineDto;
import org.unilab.uniplan.programdiscipline.ProgramDisciplineId;

import java.util.ArrayList;
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
class DisciplineServiceTest {

    @Mock
    private DisciplineRepository disciplineRepository;

    @Mock
    private DisciplineMapper disciplineMapper;

    @InjectMocks
    private DisciplineService disciplineService;

    private UUID id;
    private Integer numberOfPgIds;
    private List<ProgramDisciplineId> programDisciplineIdList = new ArrayList<>();
    private String name;
    private String mainLector;
    private DisciplineDto disciplineDto;
    private Discipline discipline;

    @BeforeEach
    void setUp(){
        id = UUID.randomUUID();
        numberOfPgIds = 2;
        for(int i = 0; i < numberOfPgIds; i ++) {
            programDisciplineIdList.add(
                new ProgramDisciplineId(
                    UUID.randomUUID(),
                    UUID.randomUUID()));
        }
        name = "OOP basics";
        mainLector = "Ivan Ivanov";
        disciplineDto = new DisciplineDto(id, name, mainLector, programDisciplineIdList);
        discipline = new Discipline();
    }

    @Test
    void testCreateDisciplineShouldSaveAndReturnDto(){
        when(disciplineMapper.toEntity(disciplineDto)).thenReturn(discipline);
        when(disciplineRepository.save(discipline)).thenReturn(discipline);
        when(disciplineMapper.toDto(discipline)).thenReturn(disciplineDto);

        DisciplineDto result = disciplineService.createDiscipline(disciplineDto);

        assertEquals(disciplineDto, result);
    }

    @Test
    void testGetAllDisciplinesShouldReturnListOfDisciplineDtos(){
        List<Discipline> disciplines = List.of(discipline);
        List<DisciplineDto> disciplineDtos = List.of(disciplineDto);

        when(disciplineRepository.findAll()).thenReturn(disciplines);
        when(disciplineMapper.toDtoList(disciplines)).thenReturn(disciplineDtos);

        List<DisciplineDto> result = disciplineService.getAllDisciplines();

        assertEquals(disciplineDtos, result);
    }

    @Test
    void testGetDisciplineByIdShouldReturnDisciplineIfFound(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.of(discipline));
        when(disciplineMapper.toDto(discipline)).thenReturn(disciplineDto);

        Optional<DisciplineDto> result = disciplineService.getDisciplineById(id);

        assertTrue(result.isPresent());
        assertEquals(disciplineDto,result.get());
    }

    @Test
    void testGetDisciplineByIdShouldReturnEmptyOptional(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.empty());

        Optional<DisciplineDto> result = disciplineService.getDisciplineById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateDisciplineShouldUpdateAndReturnDtoIfFound(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.of(discipline));
        doAnswer(invocation -> null).when(disciplineMapper)
                                    .updateEntityFromDto(disciplineDto,
                                                         discipline);
        when(disciplineRepository.save(discipline)).thenReturn(discipline);
        when(disciplineMapper.toDto(discipline)).thenReturn(disciplineDto);

        Optional<DisciplineDto> result = disciplineService.updateDiscipline(id,disciplineDto);

        assertTrue(result.isPresent());
        assertEquals(disciplineDto, result.get());
    }

    @Test
    void testUpdateLectorShouldReturnEmptyOptionalIfNotFound(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.empty());

        Optional<DisciplineDto> result = disciplineService.updateDiscipline(id, disciplineDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteDisciplineShouldDeleteDisciplineIfFound(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.of(discipline));
        doNothing().when(disciplineRepository).delete(discipline);

        assertDoesNotThrow(() -> disciplineService.deleteDiscipline(id));
        verify(disciplineRepository).delete(discipline);
    }

    @Test
    void testDeleteDisciplineShouldThrowIfNotFound(){
        when(disciplineRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> disciplineService.deleteDiscipline(id));
        assertTrue(exception.getMessage().contains(id.toString()));
    }
}