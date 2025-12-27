package ru.practicum.comment.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.model.CommentState;
import ru.practicum.comment.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.constans.StandardDateTimeFormats.DATE_TIME_FORMAT;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public Collection<FullCommentDto> getCommentsAdminForParameters(@RequestParam(required = false) @Positive Long eventId,
                                                                    @RequestParam(required = false) CommentState status,
                                                                    @RequestParam(required = false) @Positive Long userId,
                                                                    @RequestParam(required = false)
                                                                        @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                                        LocalDateTime rangeStart,
                                                                    @RequestParam(required = false)
                                                                        @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                                        LocalDateTime rangeEnd,
                                                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                    @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return commentService.getCommentsAdminForParameters(userId, eventId, status, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{commentId}")
    public FullCommentDto updateCommentStatusInComment(@PathVariable @NotNull @Positive Long commentId,
                                                       @RequestParam @NotNull CommentState state) {
        return commentService.updateCommentStatusInComment(commentId, state);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @NotNull @Positive Long commentId) {
        commentService.deleteComment(commentId);
    }
}
