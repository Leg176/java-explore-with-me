package ru.practicum.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByEventAndRequesterAndStatusNot(Event event, User requester, RequestStatus status);

    Collection<ParticipationRequest> findByRequester(User requester);

    Optional<ParticipationRequest> findByIdAndRequester(Long id, User requester);

    Collection<ParticipationRequest> findByEvent(Event event);
}
