package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.core.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRequestDto {

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 1, max = 50)
    private String title;

    private boolean pinned;

    private List<Long> events;
}
