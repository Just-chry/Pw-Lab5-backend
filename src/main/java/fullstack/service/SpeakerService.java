package fullstack.service;

import fullstack.persistence.model.Speaker;
import fullstack.persistence.repository.SpeakerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
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
    public Speaker save(Speaker speaker) {
        speaker.setId(UUID.randomUUID().toString());
        speakerRepository.persist(speaker);
        return speaker;
    }

    @Transactional
    public void deleteById(String id) {
        speakerRepository.deleteById(id);
    }

    @Transactional
    public int update(String id, Speaker speaker) {
        return speakerRepository.update(id, speaker);
    }
}
