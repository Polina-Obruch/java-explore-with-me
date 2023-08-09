package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public Compilation addCompilationAdmin(Compilation compilation, List<Long> eventIds) {
        log.info("Добавление подборки событий");

        if (eventIds != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventIds));
        }
        return compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void removeCompilationAdmin(Long compilationId) {
        log.info(String.format("Удаление подборки событий c id = %d", compilationId));
        compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation", compilationId));
        compilationRepository.deleteById(compilationId);
    }

    @Transactional
    @Override
    public Compilation updateCompilationAdmin(Long compilationId, Compilation updateCompilation, List<Long> eventIds) {
        log.info(String.format("Обновление подборки событий c id = %d", compilationId));
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation", compilationId));

        if (eventIds != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventIds));
        }

        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }

        if (updateCompilation.getTitle() != null && !updateCompilation.getTitle().isBlank()) {
            compilation.setTitle(updateCompilation.getTitle());
        }

        return compilation;
    }

    @Override
    public List<Compilation> getCompilationsPublic(Boolean pinned, Pageable pageable) {
        log.info("Выдача списка подборок событий");
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }
        //добавка статистики

        return compilations;
    }

    @Override
    public Compilation getCompilationByIdPublic(Long compilationId) {
        log.info(String.format("Выдача подборки событий c id = %d", compilationId));
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation", compilationId));
    }
}
