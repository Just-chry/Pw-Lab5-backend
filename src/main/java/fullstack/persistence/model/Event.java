package fullstack.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @Column(columnDefinition = "CHAR(36)")
    private String id;
    private String title;
    private String description;
    private LocalDate date;
    @Column(name = "partner_id")
    private String partnerId;
    @Column(name = "max_participants")
    private int maxParticipants;
    @Column(name = "participants_count")
    private int participantsCount = 0;

    public Event(String id, String title, String description, LocalDate date, String partnerId, int maxParticipants, int participantsCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.partnerId = partnerId;
        this.maxParticipants = maxParticipants;
        this.participantsCount = participantsCount;
    }

    public Event() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", partnerId='" + partnerId + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", participantsCount=" + participantsCount +
                '}';
    }
}
