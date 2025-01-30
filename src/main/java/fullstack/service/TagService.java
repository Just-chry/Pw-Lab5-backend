package fullstack.service;

import fullstack.persistence.repository.TagRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Tag;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TagService implements PanacheRepository<Tag> {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return listAll();
    }

    public Tag findById(String id) {
        return tagRepository.findById(id);
    }

    public List<Tag> getTagsByTalkId(String talkId) {
        return tagRepository.getTagsByTalkId(talkId);
    }

    @Transactional
    public Tag save(Tag tag) {
        tag.setId(UUID.randomUUID().toString());
        persist(tag);
        return tag;
    }

    @Transactional
    public void delete(String id) {
        tagRepository.deleteById(id);
    }

    @Transactional
    public int update(String id, Tag tag) {
        return tagRepository.update(id, tag);
    }
}