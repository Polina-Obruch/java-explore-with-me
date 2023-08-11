package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.StateUserAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateUserRequestDto extends EventRequestDto {
    private StateUserAction stateAction;
}
