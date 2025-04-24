package ru.practicum.ewm.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .eventName(comment.getEvent().getAnnotation())
              //  .authorName(comment.getAuthor().getName())
                .likes(comment.getLikes().size())
                .created(comment.getCreated())
                .build();
    }

  /*  public Comment toComment(NewCommentDto newCommentDto, Event event, User user) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }*/
}
