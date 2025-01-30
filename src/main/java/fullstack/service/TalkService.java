package fullstack.service;

import fullstack.persistence.model.Talk;
import fullstack.persistence.repository.TalkRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class TalkService implements PanacheRepository<Talk> {

    private final UserService userService;
    private final TalkRepository talkRepository;

    @Inject
    public TalkService(UserService userService, TalkRepository talkRepository) {
        this.userService = userService;
        this.talkRepository = talkRepository;
    }

    public List<Talk> getAllTalks() throws NoContentException {
        List<Talk> talks = listAll();
        if (talks.isEmpty()) {
            throw new NoContentException("No talks found.");
        }
        return talks;
    }

    public Talk findById(String id) throws UserNotFoundException {
        Talk talk = talkRepository.findById(id);
        if (talk == null) {
            throw new UserNotFoundException("Talk not found");
        }
        return talk;
    }

    public List<Talk> getTalksByEventId(String eventId) {
        return talkRepository.getTalksByEventId(eventId);
    }

    public List<Talk> getTalksBySpeakerId(String speakerId) throws NoContentException {
        List<Talk> talks = talkRepository.getTalksBySpeakerId(speakerId);
        if (talks.isEmpty()) {
            throw new NoContentException("No talks found for the given speaker ID.");
        }
        return talks;
    }

    public List<Talk> getTagsByTalkId(String tagId) throws NoContentException {
        List<Talk> tags = talkRepository.getTagsByTalkId(tagId);
        if (tags.isEmpty()) {
            throw new NoContentException("No tags found for the given talk ID.");
        }
        return tags;
    }

    @Transactional
    public Talk save(String sessionId, Talk talk) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        talk.setId(UUID.randomUUID().toString());
        persist(talk);
        return talk;
    }

    @Transactional
    public void deleteById(String sessionId,String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        talkRepository.deleteById(id);
    }

    @Transactional
    public void updateTalk(String sessionId, String id, Talk talk) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = update(id, talk);
        if (updated == 0) {
            throw new UserNotFoundException("Talk not found");
        }
    }
}