package fullstack.service;

import fullstack.persistence.repository.SpeakerRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Speaker;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SpeakerService implements PanacheRepository<Speaker> {
    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    public List<Speaker> getAllSpeakers() {
        return listAll();
    }

    public Speaker findById(String id) {
        return find("id", id).firstResult();
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
        persist(speaker);
        return speaker;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Speaker speaker) {
        return update("name = ?1, surname = ?2, biography = ?3 where id = ?4", speaker.getName(), speaker.getSurname(), speaker.getBiography(), id);
    }
}
