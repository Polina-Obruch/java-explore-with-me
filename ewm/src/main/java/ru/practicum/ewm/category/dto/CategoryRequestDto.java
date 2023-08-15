package ru.practicum.ewm.category.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryRequestDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
