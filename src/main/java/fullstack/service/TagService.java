package fullstack.service;

import fullstack.persistence.repository.TagRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Tag;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class TagService implements PanacheRepository<Tag> {
    private final TagRepository tagRepository;
    private  final UserService userService;

    @Inject
    public TagService(TagRepository tagRepository, UserService userService) {
        this.tagRepository = tagRepository;
        this.userService = userService;
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
    public Tag save(Tag tag) throws UserNotFoundException {
        tag.setId(UUID.randomUUID().toString());
        persist(tag);
        return tag;
    }

    @Transactional
    public void delete(String sessionId, String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        tagRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Tag tag) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        return tagRepository.update(id, tag);
    }
}