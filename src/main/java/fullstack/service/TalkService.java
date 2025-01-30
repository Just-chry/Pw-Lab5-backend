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

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

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

    public List<Talk> getAllTalks() {
        return listAll();
    }

    public Talk findById(String id) {
        return talkRepository.findById(id);
    }

    public List<Talk> getTalksByEventId(String eventId) {
        return talkRepository.getTalksByEventId(eventId);
    }

    public List<Talk> getTalksBySpeakerId(String speakerId) {
        return talkRepository.getTalksBySpeakerId(speakerId);
    }

    public List<Talk> getTagsByTalkId(String tagId) {
        return talkRepository.getTagsByTalkId(tagId);
    }


    @Transactional
    public Talk save(String sessionId, Talk talk, List<String> tagNames) throws UserNotFoundException {
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
                tagService.save(tag);
            }
            tagRepository.associateTagWithTalk(talk.getId(), tag.getId().toString());
        }

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
    public int updateTalk(String sessionId, String id, Talk talk) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        return update(id, talk);
    }
}