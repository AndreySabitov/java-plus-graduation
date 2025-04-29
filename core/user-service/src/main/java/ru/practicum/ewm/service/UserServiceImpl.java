package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.CommentClient;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.client.ParticipationRequestClient;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventClient eventClient;
    private final ParticipationRequestClient requestClient;
    private final CommentClient commentClient;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id"));

        if (ids.isEmpty()) {
            return userRepository.findAll(pageRequest).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        } else {
            return userRepository.findAllByIdIn(ids, pageRequest).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }
    }

    @Transactional
    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequest)));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        eventClient.deleteEventsByUser(id);
        requestClient.deleteRequestsOfUser(id);
        commentClient.deleteCommentsOfUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
