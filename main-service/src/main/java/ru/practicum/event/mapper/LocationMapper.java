package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto mapToLocationDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location mapToLocation(LocationDto request);
}
