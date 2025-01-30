package fullstack.service;

import fullstack.persistence.repository.EventRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public List<Event> getEventsBySpeakerId(String speakerId) {
        return eventRepository.getEventsBySpeakerId(speakerId);
    }

    public List<Event> getEventsByPartnerId(String partnerId) {
        return eventRepository.getEventsByPartnerId(partnerId);
    }

    @Transactional
    public Event save(String sessionId, Event event) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono promuovere altri utenti ad admin.");
        }
        event.setId(UUID.randomUUID().toString());
        persist(event);
        return event;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono promuovere altri utenti ad admin.");
        }
        eventRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Event event) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono promuovere altri utenti ad admin.");
        }
        return eventRepository.update(id, event);
    }
}

