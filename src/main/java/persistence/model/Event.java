package persistence.model;

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
    @Column(name = "partners_id")
    private String partnerId;

    public Event(String id, String title, String description, LocalDate date, String partnerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.partnerId = partnerId;
    }

    public Event() {

    }

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

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
