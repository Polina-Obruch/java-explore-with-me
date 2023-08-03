package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 50)
    private String name;

    @Email(message = "Введите правильный email")
    @NotBlank(message = "Email не может быть пустым")
    @Size(max = 50)
    private String email;
}
