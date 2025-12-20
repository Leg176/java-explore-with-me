package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constants.StandardDateTimeFormats.DATE_TIME_FORMAT;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "created", source = "request.created",
            dateFormat = DATE_TIME_FORMAT)
    ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest request);

    default List<ParticipationRequestDto> toFullDtoList(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(this::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
