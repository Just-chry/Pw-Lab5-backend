package fullstack.rest.model;

import fullstack.persistence.model.Event;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import java.util.List;

public class EventRequest {
    private Event event;
    private List<Talk> talks;
    private List<Tag> tags;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Talk> getTalks() {
        return talks;
    }

    public void setTalks(List<Talk> talks) {
        this.talks = talks;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}