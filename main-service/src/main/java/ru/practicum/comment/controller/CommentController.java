package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentRequest;
import ru.practicum.comment.service.CommentService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createForNotAuthorizedUser(@RequestBody @Valid NewCommentRequest request) {
        return commentService.createForNotAuthorizedUser(request);
    }

    @GetMapping("/{commentId}")
    public CommentDto getPublishedComment(@PathVariable @NotNull @Positive Long commentId) {
        return commentService.getPublishedComment(commentId);
    }

    @GetMapping("/event/{eventId}")
    public Collection<CommentDto> getPublishedCommentsForEvent(@PathVariable @NotNull @Positive Long eventId,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return commentService.getPublishedCommentsForEvent(eventId, from, size);
    }
}
