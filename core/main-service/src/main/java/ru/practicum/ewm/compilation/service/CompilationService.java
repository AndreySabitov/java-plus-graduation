package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.dto.comlication.CompilationDto;
import ru.practicum.ewm.dto.comlication.NewCompilationDto;
import ru.practicum.ewm.dto.comlication.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
