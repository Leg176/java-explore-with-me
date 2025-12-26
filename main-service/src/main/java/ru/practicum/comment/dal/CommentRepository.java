package ru.practicum.comment.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByEventAndCommentState(Event event, CommentState commentState, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.event " +
            "WHERE c.event.id = :eventId " +
            "AND (:state IS NULL OR c.commentState = :state)")
    List<Comment> findByEventIdAndCommentState(@Param("eventId") Long eventId, @Param("state") CommentState state);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.event " +
            "WHERE c.event.id IN :eventIds " +
            "AND (:state IS NULL OR c.commentState = :state)")
    List<Comment> findByStateAndEventIds(@Param("eventIds") Collection<Long> eventIds, @Param("state") CommentState state);

    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.event " +
            "WHERE (:eventId IS NULL OR c.event.id = :eventId)")
    Page<Comment> findByCommentsUserForParameters(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.event " +
            "WHERE (:userId IS NULL OR c.user.id = :userId) " +
            "AND (:eventId IS NULL OR c.event.id = :eventId)" +
            "AND (:status IS NULL OR c.commentState = :status) " +
            "AND (:rangeStart IS NULL OR c.created >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR c.created <= :rangeEnd)")
    Page<Comment> findByCommentsAdminForParameters(@Param("userId") Long userId, @Param("eventId") Long eventId,
                                                   @Param("status") CommentState status,
                                                   @Param("rangeStart") LocalDateTime rangeStart,
                                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                                   Pageable pageable);
}