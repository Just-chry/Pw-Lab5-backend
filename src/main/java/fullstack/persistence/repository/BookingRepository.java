package fullstack.persistence.repository;

import fullstack.persistence.model.Booking;
import fullstack.persistence.model.Status;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {

    public Booking findById(String id) {
        return find("id", id).firstResult();
    }

    public Booking findExistingBooking(String userId, String eventId) {
        return find("userId = ?1 and eventId = ?2 and status != ?3", userId, eventId, Status.CANCELED).firstResult();
    }

    public void persistBooking(Booking booking) {
        persist(booking);
    }

    public Booking createBooking(String userId, String eventId) {
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setDate(LocalDate.now());
        booking.setStatus(Status.CONFIRMED);
        persist(booking);
        return booking;
    }
}