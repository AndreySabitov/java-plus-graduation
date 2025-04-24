package ru.practicum.ewm.comment.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.user.UserClient;
import ru.practicum.ewm.comment.enums.SortType;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserDtoForAdmin;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto) {
      /*  checkEventId(eventId);
        Event event = checkEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new ValidationException("Нельзя комментировать не опубликованное событие");
        }
        UserDto user = checkUser(userId);
        if (user.getForbiddenCommentEvents().contains(event)) {
            throw new ValidationException("Для данного пользователя стоит запрет на комментирование данного события");
        }
        if (!event.getCommenting()) {
            throw new ValidationException("Данное событие нельзя комментировать");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, event, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));*/
        return null;
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
       /* checkEventId(eventId);
        if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!eventRepository.checkExistsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }
        Comment comment = checkComment(commentId);
        if (!Objects.equals(comment.getEvent().getId(), eventId)) {
            throw new ValidationException("Некорректно указан eventId");
        }
        if (comment.getAuthor().getId().equals(userId)) {
            comment.setText(newCommentDto.getText());
        } else {
            throw new ValidationException("Пользователь не оставлял комментарий с указанным Id " + commentId);
        }
        return CommentMapper.toCommentDto(comment);*/
        return null;
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
     /*   checkEventId(eventId);
        if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!eventRepository.checkExistsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }
        Comment comment = checkComment(commentId);
        if (!Objects.equals(comment.getEvent().getId(), eventId)) {
            throw new ValidationException("Некорректно указан eventId");
        }
        if (comment.getAuthor().getId().equals(userId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ValidationException("Пользователь не оставлял комментарий с указанным Id " + commentId);
        }*/
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, Long eventId) {
        checkEventId(eventId);
        Comment comment = checkComment(commentId);
        if (!Objects.equals(comment.getEvent().getId(), eventId)) {
            throw new ValidationException("Некорректно указан eventId");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllComments(Long eventId, SortType sortType, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<CommentDto> comments = commentRepository.findAllByEvent_Id(eventId, pageable)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
        if (sortType == SortType.LIKES) {
            return comments.stream().sorted(Comparator.comparing(CommentDto::getLikes).reversed()).toList();
        } else {
            return comments.stream().sorted(Comparator.comparing(CommentDto::getCreated).reversed()).toList();
        }
    }

    @Transactional
    @Override
    public CommentDto addLike(Long userId, Long commentId) {
    /*    if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Comment comment = checkComment(commentId);
        if (comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Пользователь не может лайкать свой комментарий");
        }
        if (!comment.getLikes().add(userId)) {
            throw new ValidationException("Нельзя поставить лайк второй раз");
        }
        return CommentMapper.toCommentDto(comment);*/
        return null;
    }

    @Transactional
    @Override
    public void deleteLike(Long userId, Long commentId) {
        if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Comment comment = checkComment(commentId);
        if (!comment.getLikes().remove(userId)) {
            throw new NotFoundException("Пользователь не лайкал комментарий с id: " + commentId);
        }
    }

    @Override
    public CommentDto getComment(Long id) {
        Comment comment = checkComment(id);
        return CommentMapper.toCommentDto(comment);
    }


    @Transactional
    @Override
    public UserDtoForAdmin addBanCommited(Long userId, Long eventId) {
      /*  checkEventId(eventId);
        UserDto user = checkUser(userId);
        Event forbidEvent = checkEvent(eventId);
        if (user.getForbiddenCommentEvents().stream().anyMatch(event -> event.getId().equals(eventId))) {
            throw new ValidationException("Уже добавлен такой запрет на комментирование");
        }
        user.getForbiddenCommentEvents().add(forbidEvent);
        return UserMapper.toUserDtoForAdmin(user);*/
        return null;
    }

    @Transactional
    @Override
    public void deleteBanCommited(Long userId, Long eventId) {
      /*  checkEventId(eventId);
        UserDto user = checkUser(userId);
        Event forbidEvent = checkEvent(eventId);
        if (!user.getForbiddenCommentEvents().remove(forbidEvent)) {
            throw new NotFoundException("Такого запрета на комментирование не найдено");
        }*/
    }

    private UserDto checkUser(Long userId) {
        try {
            return userClient.findById(userId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
    }

    private void checkEventId(Long eventId) {
        if (eventId == 0) {
            throw new ValidationException("Не задан eventId");
        }
    }

}
