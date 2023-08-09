package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation addCompilationAdmin(Compilation compilation, List<Long> eventIds);

    void removeCompilationAdmin(Long compilationId);

    Compilation updateCompilationAdmin(Long compilationId, Compilation updateCompilation, List<Long> eventIds);

    List<Compilation> getCompilationsPublic(Boolean pinned, Pageable pageable);

    Compilation getCompilationByIdPublic(Long compilationId);
}
