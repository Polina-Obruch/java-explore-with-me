package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.Marker;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @Validated({Marker.OnCreate.class})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CompilationRequestDto compilationRequestDto) {
        log.info("Запрос на создание подборки событий - admin");
        return compilationMapper.toCompilationDto(compilationService.addCompilationAdmin(
                compilationMapper.toCompilation(compilationRequestDto),
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
                                            @Valid @RequestBody CompilationRequestDto compilationRequestDto) {
        log.info("Запрос на обновление сподборки событий - admin");
        return compilationMapper.toCompilationDto(compilationService.updateCompilationAdmin(
                compilationId,
                compilationMapper.toCompilation(compilationRequestDto),
                compilationRequestDto.getEvents()));
    }
}
