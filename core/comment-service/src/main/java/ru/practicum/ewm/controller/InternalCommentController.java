package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/comment")
public class InternalCommentController {
    private final CommentService commentService;

    @DeleteMapping
    public void deleteCommentsOfUser(@RequestParam Long userId) {
        commentService.deleteCommentsOfUser(userId);
    }
}
