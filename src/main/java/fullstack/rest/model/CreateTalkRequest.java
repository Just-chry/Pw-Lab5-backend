package fullstack.rest.model;

import fullstack.persistence.model.Talk;
import java.util.List;

public class CreateTalkRequest {
    private Talk talk;
    private List<String> tagNames;

    // Getters and setters
    public Talk getTalk() {
        return talk;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }
}