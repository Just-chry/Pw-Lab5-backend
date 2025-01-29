package fullstack.persistence.repository;


import fullstack.persistence.model.Talk;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Talk;


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
}
