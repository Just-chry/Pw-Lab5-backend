package fullstack.service;

import fullstack.persistence.model.Booking;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookingService implements PanacheRepository<Booking> {
    public List<Booking> getAllBookings() {
        return listAll();
    }

    public Booking findById(String id) {
        return find("id", id).firstResult();
    }

    public Booking save(Booking booking) {
        booking.setId(UUID.randomUUID().toString());
        persist(booking);
        return booking;
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    public Booking update(String id, Booking booking) {
        update("status =?1 where id =?2", booking.getStatus(), id);
        return findById(id);
    }
}
