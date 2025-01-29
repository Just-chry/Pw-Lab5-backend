package fullstack.service;

import fullstack.persistence.model.Talk;
import fullstack.persistence.repository.TalkRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Talk;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TalkService implements PanacheRepository<Talk> {
    private final TalkRepository talkRepository;

    public TalkService(TalkRepository talkRepository) {
        this.talkRepository = talkRepository;
    }

    public List<Talk> getAllTalks() {
        return listAll();
    }

    public Talk findById(String id) {
        return find("id", id).firstResult();
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
    public Talk save(Talk talk) {
        talk.setId(UUID.randomUUID().toString());
        persist(talk);
        return talk;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public int update(String id, Talk talk) {
        return update("title = ?1, description = ?2 where id = ?3", talk.getTitle(), talk.getDescription(), id);
    }
}