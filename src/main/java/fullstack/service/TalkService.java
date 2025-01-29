package fullstack.service;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Talk;

import java.util.List;

@ApplicationScoped
public class TalkService implements PanacheRepository<Talk> {
    public List<Talk> getAllTalks() {
        return listAll();
    }

    public Talk findById(String id) {
        return find("id", id).firstResult();
    }

    public List<Talk> getTalksByEventId(String eventId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM talk t " +
                                "JOIN event_talk et ON t.id = et.talk_id " +
                                "WHERE et.event_id = :eventId", Talk.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    public List<Talk> getTalksBySpeakerId(String speakerId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM talk t " +
                                "JOIN talk_speaker et ON t.id = et.talk_id " +
                                "WHERE et.speaker_id = :speakerId", Talk.class)
                .setParameter("speakerId", speakerId)
                .getResultList();
    }

    public List<Talk> getTagsByTalkId(String tagId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM talk t " +
                                "JOIN talk_tag et ON t.id = et.talk_id " +
                                "WHERE et.tag_id = :tagId", Talk.class)
                .setParameter("tagId", tagId)
                .getResultList();
    }

    @Transactional
    public Talk save(Talk talk) {
        persist(talk);
        return talk;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Talk talk) {
        return update("title = ?1, description = ?2 where id = ?3", talk.getTitle(), talk.getDescription(), id);
    }
}