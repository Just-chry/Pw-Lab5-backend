package fullstack.persistence.repository;

import fullstack.persistence.model.Event;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {
    public List<Event> getEventsBySpeakerId(String speakerId) {
        return getEntityManager().createNativeQuery(
                        "SELECT e.* FROM event e " +
                                "JOIN event_talk et ON e.id = et.event_id " +
                                "JOIN talk_speaker ts ON et.talk_id = ts.talk_id " +
                                "WHERE ts.speaker_id = :speakerId", Event.class)
                .setParameter("speakerId", speakerId)
                .getResultList();
    }

    public List<Tag> getTagsByEventId(String eventId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM tag t " +
                                "JOIN event_tag et ON t.id = et.tag_id " +
                                "WHERE et.event_id = :eventId", Tag.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    public Event findById(String id) {
        return find("id", id).firstResult();
    }

    public List<Event> findByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return list("date", localDate);
    }

    public List<Event> getEventsByPartnerId(String partnerId) {
        return list("partnerId", partnerId);
    }

    public int update(String id, Event event) {
        return update("title = ?1, description = ?2, date = ?3 where id = ?4",
                event.getTitle(), event.getDescription(), event.getDate(), id);
    }

    public void deleteById(String id) {
        delete("id", id);
    }
}
