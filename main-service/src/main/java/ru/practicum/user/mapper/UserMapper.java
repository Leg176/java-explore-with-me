package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToUserDto(User user);

    UserShortDto mapToUserShortDto(User user);

    @Mapping(target = "id", ignore = true)
    User mapToUser(NewUserRequest request);

    default Page<UserDto> toDtoPage(Page<User> userPage) {
        return userPage.map(this::mapToUserDto);
    }
}
