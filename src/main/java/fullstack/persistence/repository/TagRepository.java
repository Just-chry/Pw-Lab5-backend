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

    public int update(String id, Tag tag) {
        return update("name = ?1 where id = ?2", tag.getName(), id);
    }

    public void deleteById(String id) {
        delete("id", id);
    }
}
