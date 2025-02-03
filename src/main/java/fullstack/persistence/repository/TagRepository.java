package fullstack.persistence.repository;

import fullstack.persistence.model.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {

    public List<Tag> getTagsByTalkId(String talkId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM tag t " +
                                "JOIN talk_tag et ON t.id = et.tag_id " +
                                "WHERE et.talk_id = :talkId", Tag.class)
                .setParameter("talkId", talkId)
                .getResultList();
    }

    public Tag findById(String id) {
        return find("id", id).firstResult();
    }

    public Tag findByName(String name) {
        return find("name", name).firstResult();
    }

    public int update(String id, Tag tag) {
        return update("name = ?1 where id = ?2", tag.getName(), id);
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    public void associateTagWithTalk(String talkId, String tagId) {
        getEntityManager().createNativeQuery(
                        "INSERT INTO talk_tag (talk_id, tag_id) VALUES (:talkId, :tagId)")
                .setParameter("talkId", talkId)
                .setParameter("tagId", tagId)
                .executeUpdate();
    }

    public void associateTagWithEvent(String eventId, String tagId) {
        getEntityManager().createNativeQuery(
                        "INSERT INTO event_tag (event_id, tag_id) VALUES (:eventId, :tagId)")
                .setParameter("eventId", eventId)
                .setParameter("tagId", tagId)
                .executeUpdate();
    }
}
