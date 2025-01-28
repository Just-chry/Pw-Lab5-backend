package fullstack.service;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Tag;

import java.util.List;

@ApplicationScoped
public class TagService implements PanacheRepository<Tag> {
    public List<Tag> getAllTags() {
        return listAll();
    }

    public Tag findById(String id) {
        return find("id", id).firstResult();
    }

    public List<Tag> getTagsByTalkId(String talkId) {
        return getEntityManager().createNativeQuery(
                        "SELECT t.* FROM tag t " +
                                "JOIN talk_tag et ON t.id = et.tag_id " +
                                "WHERE et.talk_id = :talkId", Tag.class)
                .setParameter("talkId", talkId)
                .getResultList();
    }

    @Transactional
    public Tag save(Tag tag) {
        persist(tag);
        return tag;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Tag tag) {
        return update("name = ?1 where id = ?2", tag.getName(), id);
    }
}