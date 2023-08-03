package ru.practicum.ewm.category.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryRequestDto {
    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
}
