package fullstack.persistence.repository;


import fullstack.persistence.model.Talk;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TalkRepository implements PanacheRepository<Talk> {
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

    public Talk findById(String id) {
        return find("id", id).firstResult();
    }

    public Talk findByTitle(String title) {
        return find("title", title).firstResult();
    }

    public int update(String id, Talk talk) {
        return update("title = ?1, description = ?2 where id = ?3", talk.getTitle(), talk.getDescription(), id);
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public void updateTalkSpeaker(String talkId, String speakerId) {
        getEntityManager().createNativeQuery(
                        "INSERT INTO talk_speaker (talk_id, speaker_id) VALUES (:talkId, :speakerId)")
                .setParameter("talkId", talkId)
                .setParameter("speakerId", speakerId)
                .executeUpdate();
    }

    public void associateTalkWithEvent(String eventId, String talkId) {
        getEntityManager().createNativeQuery(
                        "INSERT INTO event_talk (event_id, talk_id) VALUES (:eventId, :talkId)")
                .setParameter("eventId", eventId)
                .setParameter("talkId", talkId)
                .executeUpdate();
    }

    public int updateTalk(String id, Talk talk) {
        return update("title = ?1, description = ?2 WHERE id = ?3",
                talk.getTitle(), talk.getDescription(), id);
    }
}

