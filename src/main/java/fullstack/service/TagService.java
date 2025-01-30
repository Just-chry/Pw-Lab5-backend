package fullstack.service;

import fullstack.persistence.repository.TagRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Tag;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;
import static fullstack.util.Messages.TAG_NOT_FOUND;

@ApplicationScoped
public class TagService implements PanacheRepository<Tag> {
    private final TagRepository tagRepository;
    private  final UserService userService;

    @Inject
    public TagService(TagRepository tagRepository, UserService userService) {
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    public List<Tag> getAllTags() throws NoContentException {
        List<Tag> tags = listAll();
        if (tags.isEmpty()) {
            throw new NoContentException(TAG_NOT_FOUND);
        }
        return tags;
    }

    public Tag findById(String id) throws  NoContentException {
        Tag tag = tagRepository.findById(id);
        if (tag == null) {
            throw new NoContentException(TAG_NOT_FOUND);
        }
        return tag;
    }

    public List<Tag> getTagsByTalkId(String talkId) throws NoContentException {
        List<Tag> tags = tagRepository.getTagsByTalkId(talkId);
        if (tags.isEmpty()) {
            throw new NoContentException(TAG_NOT_FOUND);
        }
        return tags;
    }

    @Transactional
    public Tag save(String sessionId, Tag tag) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        tag.setId(UUID.randomUUID().toString());
        persist(tag);
        return tag;
    }

    @Transactional
    public void delete(String sessionId, String id) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        tagRepository.deleteById(id);
    }

    @Transactional
    public void update(String sessionId, String id, Tag tag) throws NoContentException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = tagRepository.update(id, tag);
        if (updated == 0) {
            throw new NoContentException(TAG_NOT_FOUND);
        }
    }
}