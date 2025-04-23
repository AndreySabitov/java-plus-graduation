package ru.practicum.ewm.client.user;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping
    List<UserDto> getAllUsers(@RequestParam(defaultValue = "") List<Long> ids,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                              @Positive @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @PostMapping
    UserDto saveUser(@RequestBody @Valid NewUserRequest newUserRequest) throws FeignException;

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId) throws FeignException;
}
