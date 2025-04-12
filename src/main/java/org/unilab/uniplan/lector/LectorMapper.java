package org.unilab.uniplan.lector;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LectorMapper {

    @Mapping(source = "faculty.id", target = "facultyId")
    LectorDto toDto(Lector lector);

    @Mapping(source = "facultyId", target = "faculty")
    Lector toEntity(LectorDto lectorDto);

    @Mapping(source = "facultyId", target = "faculty")
    void updateEntity(LectorDto dto, @MappingTarget Lector entity);

    List<LectorDto> toDtos(List<Lector> lectors);
}
