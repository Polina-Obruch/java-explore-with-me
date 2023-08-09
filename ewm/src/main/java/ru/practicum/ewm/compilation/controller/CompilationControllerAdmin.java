package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CompilationRequestDto compilationRequestDto) {
        log.info("Запрос на создание подборки событий - admin");
        return compilationMapper.compilationToCompilationDto(compilationService.addCompilationAdmin(
                compilationMapper.compilationRequestDtoToCompilation(compilationRequestDto),
                compilationRequestDto.getEvents()));
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compilationId) {
        log.info("Запрос на удаление подборки событий - admin");
        compilationService.removeCompilationAdmin(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId,
                                            @Valid @RequestBody CompilationUpdateDto compilationUpdateDto) {
        log.info("Запрос на обновление сподборки событий - admin");
        return compilationMapper.compilationToCompilationDto(compilationService.updateCompilationAdmin(
                compilationId,
                compilationMapper.compilationUpdateDtoToCompilation(compilationUpdateDto),
                compilationUpdateDto.getEvents()));
    }
}
