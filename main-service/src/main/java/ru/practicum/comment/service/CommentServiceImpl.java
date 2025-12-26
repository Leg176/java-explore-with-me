package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dal.CommentRepository;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentState;
import ru.practicum.error.exceptions.BadRequestException;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.comment.model.CommentState.PENDING;
import static ru.practicum.event.model.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createForNotAuthorizedUser(NewCommentRequest request) {

        if (request == null) {
            throw new BadRequestException("Запрос на добавление нового комментария не может быть null");
        }

        Event event = isContainsEvent(request.getEventId());

        if (event.getEventState() != PUBLISHED) {
            throw new ConflictException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = commentMapper.mapToComment(request);
        comment.setCommentState(PENDING);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        repository.save(comment);

        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    public CommentDto getPublishedComment(Long commentId) {
        Comment comment = isContainComment(commentId);

        if (comment.getCommentState() != CommentState.PUBLISHED) {
            throw new NotFoundException("Комментарий не найден");
        }

        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    public Collection<CommentDto> getPublishedCommentsForEvent(Long eventId, Integer from, Integer size) {
        Event event = isContainsEvent(eventId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").ascending());

        Page<Comment> commentPage = repository.findByEventAndCommentState(event, CommentState.PUBLISHED, pageable);
        List<Comment> comments = commentPage.getContent();
        return commentMapper.mapToCommentsList(comments);
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, NewCommentForAuthUserRequest request) {
        User user = isContainsUser(userId);

        if (request == null) {
            throw new BadRequestException("Запрос на добавление нового комментария не может быть null");
        }

        Event event = isContainsEvent(request.getEventId());

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Нельзя комментировать событие созданное самим");
        }

        if (event.getEventState() != PUBLISHED) {
            throw new ConflictException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = commentMapper.mapToComment(request);
        comment.setCommentState(CommentState.PUBLISHED);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        comment.setUser(user);
        repository.save(comment);

        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        Comment comment = isContainComment(commentId);

        if (request == null) {
            throw new BadRequestException("Запрос на изменения комментария не может быть null");
        }

        if (comment.getCommentState() == CommentState.CANCELED) {
            throw new ConflictException("Нельзя изменять отменённые комментарии");
        }

        if (comment.getUser() != null && !comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Нельзя изменять чужие комментарии");
        }

        commentMapper.updateFromRequestUser(request, comment);
        comment.setUpdated(LocalDateTime.now());

        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public FullCommentDto cancelComment(Long userId, Long commentId) {
        Comment comment = isContainComment(commentId);

        if (comment.getCommentState() == CommentState.CANCELED) {
            throw new ConflictException("Нельзя изменять отменённые комментарии");
        }

        if (comment.getUser() != null || !comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Нельзя изменять чужие комментарии");
        }

        if (comment.getCommentState() != CommentState.PUBLISHED) {
            throw new ConflictException("Можно отменять только опубликованные комментарии");
        }

        comment.setCommentState(CommentState.CANCELED);
        comment.setUpdated(LocalDateTime.now());

        return commentMapper.mapToFullCommentDto(comment);
    }

    @Override
    public Collection<FullCommentDto> getCommentsUserForParameters(Long userId, Long eventId,
                                                                   Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").ascending());

        isContainsUser(userId);

        if (eventId != null) {
            Event event = isContainsEvent(eventId);
            if (!event.getInitiator().getId().equals(userId)) {
                throw new ConflictException("Вы не можете просматривать все комментарии этого события" +
                        " так как не являетесь создателем события");
            }
        }

        Page<Comment> commentPage = repository.findByCommentsUserForParameters(userId, eventId, pageable);
        List<Comment> comments = commentPage.getContent();

        return commentMapper.mapToFullCommentsList(comments);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = isContainComment(commentId);

        repository.delete(comment);
    }

    @Override
    @Transactional
    public FullCommentDto updateCommentStatusInComment(Long commentId, CommentState state) {
        Comment comment = isContainComment(commentId);

        if (comment.getCommentState() == CommentState.CANCELED) {
            throw new ConflictException("Нельзя изменять статус отменённым комментариям");
        }

        if (comment.getCommentState() == CommentState.PUBLISHED && state != CommentState.CANCELED) {
            throw new ConflictException("Опубликованный комментарий можно только отменить");
        }

        comment.setCommentState(state);
        comment.setUpdated(LocalDateTime.now());

        return commentMapper.mapToFullCommentDto(comment);
    }

    @Override
    public Collection<FullCommentDto> getCommentsAdminForParameters(Long userId, Long eventId, CommentState status,
                                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                                    Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата старта выборки не может быть позже даты окончания выборки");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").ascending());
        Page<Comment> commentPage = repository.findByCommentsAdminForParameters(userId, eventId, status,
                rangeStart, rangeEnd, pageable);

        List<Comment> comments = commentPage.getContent();

        return commentMapper.mapToFullCommentsList(comments);
    }

    private Event isContainsEvent(Long id) {
        Optional<Event> optEvent = eventRepository.findById(id);

        if (optEvent.isEmpty()) {
            throw new NotFoundException("Событие с id: " + id + " в базе отсутствует");
        }

        return optEvent.get();
    }

    private Comment isContainComment(Long id) {
        Optional<Comment> optComment = repository.findById(id);

        if (optComment.isEmpty()) {
            throw new NotFoundException("Комментарий с id: " + id + " в базе отсутствует");
        }

        return optComment.get();
    }

    private User isContainsUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);

        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }

        return optUser.get();
    }

    private void checkRequestAddComment() {

    }
}
