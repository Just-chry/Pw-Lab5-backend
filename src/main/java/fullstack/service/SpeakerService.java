package fullstack.service;

import fullstack.persistence.model.Speaker;
import fullstack.persistence.repository.SpeakerRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final UserService userService;
    @Inject
    public SpeakerService(SpeakerRepository speakerRepository, UserService userService) {
        this.speakerRepository = speakerRepository;
        this.userService = userService;
    }

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.listAll();
    }

    public Speaker findById(String id) {
        return speakerRepository.findById(id);
    }

    public List<Speaker> getSpeakerByTalkId(String talkId) {
        return speakerRepository.getSpeakerByTalkId(talkId);
    }

    public List<Speaker> getSpeakersByEventId(String eventId) {
        return speakerRepository.getSpeakersByEventId(eventId);
    }

    @Transactional
    public Speaker save(String sessionId, Speaker speaker) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        speaker.setId(UUID.randomUUID().toString());
        speakerRepository.persist(speaker);
        return speaker;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        speakerRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Speaker speaker) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        return speakerRepository.update(id, speaker);
    }
}
