package fullstack.service;

import fullstack.persistence.repository.EventRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Event;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class EventService implements PanacheRepository<Event> {
    private final EventRepository eventRepository;
    private final UserService userService;

    @Inject
    public EventService(EventRepository eventRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public List<Event> getAllEvents() throws NoContentException {
        List<Event> events = listAll();
        if (events.isEmpty()) {
            throw new NoContentException("No events found.");
        }
        return events;
    }

    public Event findById(String id) throws NoContentException {
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new NoContentException("Event not found");
        }
        return event;
    }

    public List<Event> findByDate(String date) throws NoContentException {
        List<Event> events = eventRepository.findByDate(date);
        if (events.isEmpty()) {
            throw new NoContentException("No events found for the given date.");
        }
        return events;
    }

    public List<Event> getEventsBySpeakerId(String speakerId) throws NoContentException {
        List<Event> events = eventRepository.getEventsBySpeakerId(speakerId);
        if (events.isEmpty()) {
            throw new NoContentException("No events found for the given speaker ID.");
        }
        return events;
    }

    public List<Event> getEventsByPartnerId(String partnerId) throws NoContentException {
        List<Event> events = eventRepository.getEventsByPartnerId(partnerId);
        if (events.isEmpty()) {
            throw new NoContentException("No events found for the given partner ID.");
        }
        return events;
    }

    @Transactional
    public Event save(String sessionId, Event event) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        event.setId(UUID.randomUUID().toString());
        persist(event);
        return event;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        eventRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Event event) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = eventRepository.update(id, event);
        if (updated == 0) {
            throw new SessionException("Event not found");
        }
        return updated;
    }
}

