package fullstack.persistence.repository;

import fullstack.persistence.model.Speaker;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SpeakerRepository implements PanacheRepository<Speaker> {

    public Speaker findById(String id) {
        return find("id", id).firstResult();
    }

    public Speaker findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    public int update(String id, Speaker speaker) {
        return update("name = ?1, surname = ?2, biography = ?3 where id = ?4",
                speaker.getName(), speaker.getSurname(), speaker.getBiography(), id);
    }

    public List<Speaker> getSpeakerByTalkId(String talkId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM speaker t " +
                                "JOIN talk_speaker et ON t.id = et.speaker_id " +
                                "WHERE et.talk_id = :talkId", Speaker.class)
                .setParameter("talkId", talkId)
                .getResultList();
    }

    public List<Speaker> getSpeakersByEventId(String eventId) {
        return getEntityManager().createNativeQuery(
                        "SELECT s.* FROM speaker s " +
                                "JOIN talk_speaker ts ON s.id = ts.speaker_id " +
                                "JOIN talk t ON ts.talk_id = t.id " +
                                "JOIN event_talk et ON t.id = et.talk_id " +
                                "WHERE et.event_id = :eventId", Speaker.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}
