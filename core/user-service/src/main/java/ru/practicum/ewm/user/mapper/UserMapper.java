package ru.practicum.ewm.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserDtoForAdmin;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.util.HashSet;

@UtilityClass
public class UserMapper {
    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .forbiddenCommentEvents(new HashSet<>())
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public UserDtoForAdmin toUserDtoForAdmin(User user) {
        return UserDtoForAdmin.builder()
                .id(user.getId())
                .name(user.getName())
                //.forbiddenCommentEvents(user.getForbiddenCommentEvents().stream().map(Event::getId).collect(Collectors.toSet()))
                .email(user.getEmail())
                .build();
    }
}
