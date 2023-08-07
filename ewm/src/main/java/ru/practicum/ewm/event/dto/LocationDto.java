package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @NotNull
    @Digits(integer = 5, fraction = 4)
    private BigDecimal lat;

    @NotNull
    @Digits(integer = 5, fraction = 4)
    private BigDecimal lon;
}
