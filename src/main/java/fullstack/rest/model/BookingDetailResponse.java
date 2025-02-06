package fullstack.rest.model;

import fullstack.persistence.model.Status;

import java.time.LocalDateTime;

public class BookingDetailResponse {
    private String bookingId;
    private String title;
    private LocalDateTime date;
    private Status status;

    public BookingDetailResponse(String bookingId, String title, LocalDateTime date, Status status) {
        this.bookingId = bookingId;
        this.title = title;
        this.date = date;
        this.status = status;
    }

    // Getter e setter
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
