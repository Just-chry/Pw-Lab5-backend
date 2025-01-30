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

    public List<Event> getAllEvents() {
        return listAll();
    }

    public Event findById(String id) {
        return eventRepository.findById(id);
    }

    public List<Event> findByDate(String date) {
        return eventRepository.findByDate(date);
    }

    public List<Event> getEventsBySpeakerId(String speakerId) throws NoContentException {
        List<Event> events = eventRepository.getEventsBySpeakerId(speakerId);
        if (events.isEmpty()) {
            throw new NoContentException("No events found for the given speaker ID.");
        }
        return events;
    }

    public List<Event> getEventsByPartnerId(String partnerId) {
        return eventRepository.getEventsByPartnerId(partnerId);
    }

    @Transactional
    public Event save(String sessionId, Event event) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        event.setId(UUID.randomUUID().toString());
        persist(event);
        return event;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        eventRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Event event) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = eventRepository.update(id, event);
        if (updated == 0) {
            throw new UserNotFoundException("Event not found");
        }
        return updated;
    }
}

