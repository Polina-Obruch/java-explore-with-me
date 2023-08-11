package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.StateAdminAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateAdminRequestDto extends EventRequestDto {
    private StateAdminAction stateAction;
}
