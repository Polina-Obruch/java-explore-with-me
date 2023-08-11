package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.core.utils.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(groups = {Marker.OnCreate.class})
    private Long category;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 3, max = 120)
    private String title;

    @NotNull(groups = {Marker.OnCreate.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    @NotNull(groups = {Marker.OnCreate.class})
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;
}
