package fullstack.service;

import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.persistence.model.Ticket;
import fullstack.persistence.repository.EventRepository;
import fullstack.persistence.repository.TagRepository;
import fullstack.persistence.repository.TalkRepository;
import fullstack.persistence.repository.TicketRepository;
import fullstack.service.exception.AdminAccessException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Event;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.*;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class EventService implements PanacheRepository<Event> {
    @Inject
    EventRepository eventRepository;
    @Inject
    UserService userService;
    @Inject
    TalkRepository talkRepository;
    @Inject
    TagRepository tagRepository;
    @Inject
    TicketService ticketService;

    public List<Event> getAllEvents() throws NoContentException {
        List<Event> events = listAll();
        if (events.isEmpty()) {
            throw new NoContentException(EVENT_NOT_FOUND);
        }
        return events;
    }

    public Event findById(String id) throws NoContentException {
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new NoContentException(EVENT_NOT_FOUND);
        }
        return event;
    }

    public List<Event> findByDate(String date) throws NoContentException {
        List<Event> events = eventRepository.findByDate(date);
        if (events.isEmpty()) {
            throw new NoContentException(EVENT_NOT_FOUND);
        }
        return events;
    }

    public List<Event> getEventsBySpeakerId(String speakerId) throws NoContentException {
        List<Event> events = eventRepository.getEventsBySpeakerId(speakerId);
        if (events.isEmpty()) {
            throw new NoContentException(SPEAKER_EVENT_NOT_FOUND);
        }
        return events;
    }

    public List<Event> getEventsByPartnerId(String partnerId) throws NoContentException {
        List<Event> events = eventRepository.getEventsByPartnerId(partnerId);
        if (events.isEmpty()) {
            throw new NoContentException(SPEAKER_EVENT_NOT_FOUND);
        }
        return events;
    }

    @Transactional
    public Event save(String sessionId, Event event, List<Talk> talks, List<Tag> tags) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }

        event.setId(UUID.randomUUID().toString());
        persist(event);

        for (Talk talk : talks) {
            Talk existingTalk = talkRepository.findByTitle(talk.getTitle());
            if (existingTalk == null) {
                talk.setId(UUID.randomUUID().toString());
                talkRepository.persist(talk);
            } else {
                talk = existingTalk;
            }
            talkRepository.associateTalkWithEvent(event.getId(), talk.getId());
        }

        for (Tag tag : tags) {
            Tag existingTag = tagRepository.findByName(tag.getName());
            if (existingTag == null) {
                tag.setId(UUID.randomUUID().toString());
                tagRepository.persist(tag);
            } else {
                tag = existingTag;
            }
            tagRepository.associateTagWithEvent(event.getId(), tag.getId());
        }
        ticketService.createTicketsForEvent(event.getId(), event.getMaxParticipants());

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
    public int update(String sessionId, String id, Event event) throws SessionException, NoContentException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = eventRepository.update(id, event);
        if (updated == 0) {
            throw new NoContentException(EVENT_NOT_FOUND);
        }
        return updated;
    }
}

