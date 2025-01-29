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
        return find("id", id).firstResult();
    }

    public List<Event> findByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return list("date", localDate);
    }

    public List<Event> getEventsBySpeakerId(String speakerId) {
        return eventRepository.getEventsBySpeakerId(speakerId);
    }

    public List<Event> getEventsByPartnerId(String partnerId) {
        return list("partnerId", partnerId);
    }

    @Transactional
    public Event save(Event event) {
        event.setId(UUID.randomUUID().toString());
        persist(event);
        return event;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Event event) {
        return update("title = ?1, description = ?2, date = ?3 where id = ?4", event.getTitle(), event.getDescription(), event.getDate(), id);
    }
}

