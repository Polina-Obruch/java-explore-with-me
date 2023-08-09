package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRequestDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private boolean pinned = false;

    private List<Long> events;
}
