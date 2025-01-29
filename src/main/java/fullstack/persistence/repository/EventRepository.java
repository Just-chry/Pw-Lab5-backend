package fullstack.persistence.repository;

import fullstack.persistence.model.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

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
}
