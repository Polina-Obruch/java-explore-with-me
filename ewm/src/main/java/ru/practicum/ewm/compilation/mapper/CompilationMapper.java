package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(CompilationRequestDto compilationRequestDto);

    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toListCompilationDto(List<Compilation> compilations);
}
