package fullstack.service;

import fullstack.persistence.repository.EventRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventService implements PanacheRepository<Event> {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
    public Event save(Event event) {
        event.setId(UUID.randomUUID().toString());
        persist(event);
        return event;
    }

    @Transactional
    public void deleteById(String id) {
        eventRepository.deleteById(id);
    }

        @Transactional
    public int update(String id, Event event) {
            return eventRepository.update(id, event);
    }
}

