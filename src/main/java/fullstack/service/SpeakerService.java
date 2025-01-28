package fullstack.service;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Speaker;

import java.util.List;

@ApplicationScoped
public class SpeakerService implements PanacheRepository<Speaker> {
    public List<Speaker> getAllSpeakers() {
        return listAll();
    }

    public Speaker findById(String id) {
        return find("id", id).firstResult();
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

    @Transactional
    public Speaker save(Speaker speaker) {
        persist(speaker);
        return speaker;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Speaker speaker) {
        return update("name = ?1, surname = ?2, biography = ?3 where id = ?4", speaker.getName(), speaker.getSurname(), speaker.getBiography(), id);
    }
}
