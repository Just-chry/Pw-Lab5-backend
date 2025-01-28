package fullstack.service;

import fullstack.persistence.model.Booking;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class BookingService implements PanacheRepository<Booking> {
    public List<Booking> getAllBookings() {
        return listAll();
    }

    public Booking findById(String id) {
        return find("id", id).firstResult();
    }

    public Booking save(Booking booking) {
        persist(booking);
        return booking;
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    public int update(String id, Booking booking) {
        return update("status =?1, where id =?3", booking.getStatus(), id);
    }
}
