package ru.practicum.ewm.request.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerStatusUpdate {
    private List<Request> confirmedRequests;
    private List<Request> rejectedRequests;
}
