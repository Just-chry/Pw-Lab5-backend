package fullstack.persistence.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(columnDefinition = "CHAR(36)")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "event_id")
    private String eventId;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    public Booking(String id, String userId, String eventId, LocalDate date, Status status) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.date = date;
        this.status = status;
    }

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
