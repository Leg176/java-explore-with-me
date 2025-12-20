package ru.practicum.event.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constants.StandardDateTimeFormats.DATE_TIME_FORMAT;
import static ru.practicum.event.model.EventState.PENDING;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "eventDate", source = "event.eventDate",
            dateFormat = DATE_TIME_FORMAT)
    EventShortDto mapToEventShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", source = "request.eventDate", dateFormat = DATE_TIME_FORMAT)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "eventState", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event mapToEvent(NewEventDto request);

    @AfterMapping
    default void setDefaults(@MappingTarget Event event) {
        if (event.getCreatedOn() == null) event.setCreatedOn(LocalDateTime.now());
        if (event.getConfirmedRequests() == null) event.setConfirmedRequests(0L);
        if (event.getEventState() == null) event.setEventState(PENDING);
        if (event.getViews() == null) event.setViews(0L);
    }

    @Mapping(target = "createdOn", source = "event.createdOn", dateFormat = DATE_TIME_FORMAT)
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = DATE_TIME_FORMAT)
    @Mapping(target = "publishedOn", source = "event.publishedOn", dateFormat = DATE_TIME_FORMAT)
    @Mapping(target = "state", source = "event.eventState")
    EventFullDto mapToEventFullDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventState", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateFromRequestUser(UpdateEventUserRequest request, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventState", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateFromRequestAdmin(UpdateEventAdminRequest request, @MappingTarget Event event);

    default Page<EventShortDto> toDtoPage(Page<Event> eventPage) {
        return eventPage.map(this::mapToEventShortDto);
    }

    default Page<EventFullDto> toFullDtoPage(Page<Event> eventPage) {
        return eventPage.map(this::mapToEventFullDto);
    }

    default List<EventFullDto> toFullDtoList(List<Event> events) {
        return events.stream()
                .map(this::mapToEventFullDto)
                .collect(Collectors.toList());
    }
}
