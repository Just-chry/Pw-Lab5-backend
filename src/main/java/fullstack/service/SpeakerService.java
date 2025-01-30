package fullstack.service;

import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.User;
import fullstack.persistence.repository.SpeakerRepository;
import fullstack.persistence.repository.UserRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;
import static fullstack.util.Messages.SPEAKER_NOT_FOUND;

@ApplicationScoped
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Inject
    public SpeakerService(SpeakerRepository speakerRepository, UserService userService, UserRepository userRepository) {
        this.speakerRepository = speakerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<Speaker> getAllSpeakers() throws NoContentException {
        List<Speaker> speakers = speakerRepository.listAll();
        if (speakers.isEmpty()) {
            throw new NoContentException(SPEAKER_NOT_FOUND);
        }
        return speakers;
    }

    public Speaker findById(String id) throws UserNotFoundException {
        Speaker speaker = speakerRepository.findById(id);
        if (speaker == null) {
            throw new UserNotFoundException(SPEAKER_NOT_FOUND);
        }
        return speaker;
    }

    public List<Speaker> getSpeakerByTalkId(String talkId) throws NoContentException {
        List<Speaker> speakers = speakerRepository.getSpeakerByTalkId(talkId);
        if (speakers.isEmpty()) {
            throw new NoContentException(SPEAKER_NOT_FOUND);
        }
        return speakers;
    }

    public List<Speaker> getSpeakersByEventId(String eventId) throws NoContentException {
        List<Speaker> speakers = speakerRepository.getSpeakersByEventId(eventId);
        if (speakers.isEmpty()) {
            throw new NoContentException(SPEAKER_NOT_FOUND);
        }
        return speakers;
    }

    @Transactional
    public Speaker save(String sessionId, Speaker speaker) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        speaker.setId(UUID.randomUUID().toString());
        speakerRepository.persist(speaker);
        return speaker;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        speakerRepository.deleteById(id);
    }

    @Transactional
    public void update(String sessionId, String id, Speaker speaker) throws NoContentException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = speakerRepository.update(id, speaker);
        if (updated == 0) {
            throw new NoContentException(SPEAKER_NOT_FOUND);
        }
    }

    @Transactional
    public Speaker findOrCreateSpeaker(String userId) throws UserNotFoundException {
        Speaker s = speakerRepository.findByUserId(userId);
        if (s == null) {
            Optional<User> user = userRepository.findUserById(userId);
            if (user.isEmpty()) {
                throw new UserNotFoundException("User not found");
            }
            s = new Speaker();
            s.setId(UUID.randomUUID().toString());
            s.setUserId(userId);
            s.setName(user.get().getName());
            s.setSurname(user.get().getSurname());
            speakerRepository.persist(s);
        }
        return s;
    }
}
