package fullstack.service;

import fullstack.persistence.model.Booking;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookingService implements PanacheRepository<Booking> {
    private final UserService userService;

    @jakarta.inject.Inject
    public BookingService(UserService userService) {
        this.userService = userService;
    }

    public List<Booking> getAllBookings() {
        return listAll();
    }

    public Booking findById(String id) {
        return find("id", id).firstResult();
    }

    @Transactional
    public Booking save(Booking booking, String sessionId) throws UserNotFoundException {
        booking.setId(UUID.randomUUID().toString());
        booking.setUserId(userService.getUserIdBySessionId(sessionId));
        booking.setDate(LocalDate.now());
        persist(booking);
        return booking;
    }

    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Transactional
    public Booking updateStatus(String id, String status) {
        update("status =?1 where id =?2", status, id);
        return findById(id);
    }
    public Booking update(String id, Booking booking) {
        update("status =?1 where id =?2", booking.getStatus(), id);
        return findById(id);
    }
}
