package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.service.CommentService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @NotNull @Positive Long userId,
                                    @RequestBody @Valid NewCommentForAuthUserRequest request) {
        return commentService.createComment(userId, request);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable @NotNull @Positive Long userId,
                                    @PathVariable @NotNull @Positive Long commentId,
                                    @RequestBody @Valid UpdateCommentRequest request) {

        return commentService.updateComment(userId, commentId, request);
    }

    @PatchMapping("/{commentId}/cancel")
    public FullCommentDto cancelComment(@PathVariable @NotNull @Positive Long userId,
                                        @PathVariable @NotNull @Positive Long commentId) {

        return commentService.cancelComment(userId, commentId);
    }

    @GetMapping
    public Collection<FullCommentDto> getCommentsUserForParameters(@PathVariable @NotNull @Positive Long userId,
                                                                   @RequestParam(required = false) Long eventId,
                                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return commentService.getCommentsUserForParameters(userId, eventId, from, size);
    }
}
