package fullstack.rest.model;

import fullstack.persistence.model.Event;
import fullstack.persistence.model.Talk;
import java.util.List;

public class EventRequest {
    private Event event;
    private List<Talk> talks;

    // Getters and setters
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
}