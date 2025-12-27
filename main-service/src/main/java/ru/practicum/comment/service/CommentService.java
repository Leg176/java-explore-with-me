package ru.practicum.comment.service;

import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.CommentState;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CommentService {

    CommentDto createForNotAuthorizedUser(NewCommentRequest request);

    CommentDto getPublishedComment(Long commentId);

    Collection<CommentDto> getPublishedCommentsForEvent(Long eventId, Integer from, Integer size);

    CommentDto createComment(Long userId, NewCommentForAuthUserRequest request);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request);

    FullCommentDto cancelComment(Long userId, Long commentId);

    Collection<FullCommentDto> getCommentsUserForParameters(Long userId, Long eventId,
                                                                     Integer from, Integer size);

    void deleteComment(Long commentId);

    FullCommentDto updateCommentStatusInComment(Long commentId, CommentState state);

    Collection<FullCommentDto> getCommentsAdminForParameters(Long userId, Long eventId, CommentState status,
                                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                             Integer from, Integer size);
}
