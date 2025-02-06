package fullstack.service;

import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.persistence.model.User;
import fullstack.persistence.repository.TagRepository;
import fullstack.persistence.repository.TalkRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;
import static fullstack.util.Messages.TALK_NOT_FOUND;

@ApplicationScoped
public class TalkService implements PanacheRepository<Talk> {

    @Inject
    UserService userService;
    @Inject
    TalkRepository talkRepository;
    @Inject
    SpeakerService speakerService;
    @Inject
    TagRepository tagRepository;
    @Inject
    TagService tagService;

    public List<Talk> getAllTalks() throws NoContentException {
        List<Talk> talks = listAll();
        if (talks.isEmpty()) {
            throw new NoContentException(TALK_NOT_FOUND);
        }
        return talks;
    }

    public Talk findById(String id) throws UserNotFoundException {
        Talk talk = talkRepository.findById(id);
        if (talk == null) {
            throw new UserNotFoundException(TALK_NOT_FOUND);
        }
        return talk;
    }

    public List<Talk> getTalksByEventId(String eventId) throws NoContentException {
        List<Talk> talks = talkRepository.getTalksByEventId(eventId);
        if (talks.isEmpty()) {
            throw new NoContentException(TALK_NOT_FOUND);
        }
        return talks;
    }

    public List<Talk> getTalksBySpeakerId(String speakerId) throws NoContentException {
        List<Talk> talks = talkRepository.getTalksBySpeakerId(speakerId);
        if (talks.isEmpty()) {
            throw new NoContentException(TALK_NOT_FOUND);
        }
        return talks;
    }

    public List<Talk> getTagsByTalkId(String tagId) throws NoContentException {
        List<Talk> tags = talkRepository.getTagsByTalkId(tagId);
        if (tags.isEmpty()) {
            throw new NoContentException(TALK_NOT_FOUND);
        }
        return tags;
    }

    @Transactional
    public Talk save(String sessionId, Talk talk, List<String> tagNames) throws SessionException, UserNotFoundException {
        if (talk == null) {
            throw new IllegalArgumentException("Talk cannot be null");
        }
        User user = userService.getUserBySessionId(sessionId);
        String userId = user.getId();
        Speaker speaker = speakerService.findOrCreateSpeaker(userId);
        talk.setId(UUID.randomUUID().toString());
        talkRepository.persist(talk);
        talkRepository.updateTalkSpeaker(talk.getId(), speaker.getId());

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tagService.save(sessionId, tag);
            }
            tagRepository.associateTagWithTalk(talk.getId(), tag.getId());
        }

        return talk;
    }

    @Transactional
    public void deleteById(String sessionId,String id) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        talkRepository.deleteById(id);
    }

    @Transactional
    public void updateTalk(String sessionId, String id, Talk talk) throws NoContentException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = talkRepository.updateTalk(id, talk);
        if (updated == 0) {
            throw new NoContentException(TALK_NOT_FOUND);
        }
    }
}