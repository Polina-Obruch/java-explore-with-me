package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation compilationRequestDtoToCompilation(CompilationRequestDto compilationRequestDto);

    CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation compilationUpdateDtoToCompilation(CompilationUpdateDto compilationUpdateDto);

    List<CompilationDto> listCompilationToListCompilationDto(List<Compilation> compilations);
}
