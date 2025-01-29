package fullstack.service;

import fullstack.persistence.model.Booking;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Status;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookingService implements PanacheRepository<Booking> {
    private final UserService userService;
    private final EventService eventService;

    @jakarta.inject.Inject
    public BookingService(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    public List<Booking> getAllBookings() {
        return listAll();
    }

    public Booking findById(String id) {
        return find("id", id).firstResult();
    }

    @Transactional
    public Booking save(String sessionId, String eventId) throws UserNotFoundException {
        String userId = userService.getUserIdBySessionId(sessionId);

        // Check if the event exists
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }

        Booking existingBooking = find("userId = ?1 and eventId = ?2 and status != ?3", userId, eventId, Status.canceled).firstResult();
        if (existingBooking != null) {
            throw new RuntimeException("User has already booked this event");
        }

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setDate(LocalDate.now());
        persist(booking);
        return booking;
    }

    @Transactional
    public Booking confirmBooking(String id) {
        Booking booking = findById(id);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found with id: " + id);
        }

        Event event = eventService.findById(booking.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Event not found with id: " + booking.getEventId());
        }

        if (event.getParticipantsCount() >= event.getMaxParticipants()) {
            throw new RuntimeException("Cannot confirm booking: event is fully booked");
        }

        booking.setStatus(Status.confirmed);
        persist(booking);

        event.setParticipantsCount(event.getParticipantsCount() + 1);
        eventService.persist(event);

        return booking;
    }

    @Transactional
    public Booking declineBooking(String id) {
        Booking booking = findById(id);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found with id: " + id);
        }
        booking.setStatus(Status.declined);
        persist(booking);
        return booking;
    }

    @Transactional
    public Booking cancelBooking(String id) {
        Booking booking = findById(id);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found with id: " + id);
        }

        Event event = eventService.findById(booking.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Event not found with id: " + booking.getEventId());
        }

        if (booking.getStatus() == Status.confirmed) {
            event.setParticipantsCount(event.getParticipantsCount() - 1);
            eventService.persist(event);
        }

        booking.setStatus(Status.canceled);
        persist(booking);

        return booking;
    }
}
