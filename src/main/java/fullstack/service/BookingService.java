package fullstack.service;

import fullstack.persistence.model.Booking;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Status;
import fullstack.persistence.model.User;
import fullstack.persistence.repository.BookingRepository;
import fullstack.persistence.repository.UserRepository;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@ApplicationScoped
public class BookingService implements PanacheRepository<Booking> {
    @Inject
    UserService userService;
    @Inject
    EventService eventService;
    @Inject
    BookingRepository bookingRepository;
    @Inject
    NotificationService notificationService;
    @Inject
    UserRepository userRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.listAll();
    }

    public Booking findById(String id) {
        return bookingRepository.findById(id);
    }


    @Transactional
    public Booking save(String sessionId, String eventId) throws UserNotFoundException {
        User user = userService.getUserBySessionId(sessionId);
        String userId = user.getId();

        // Check if the event exists
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }

        Booking existingBooking = bookingRepository.findExistingBooking(userId, eventId);
        if (existingBooking != null) {
            throw new RuntimeException("User has already booked this event");
        }

        if (event.getParticipantsCount() >= event.getMaxParticipants()) {
            throw new RuntimeException("Cannot confirm booking: event is fully booked");
        }

        Booking booking = bookingRepository.createBooking(userId, eventId);

        event.setParticipantsCount(event.getParticipantsCount() + 1);
        eventService.persist(event);

        // Send confirmation email
        if (user.getEmail() == null  || user.getEmail().isEmpty() && user.getPhone() != null && !user.getPhone().isEmpty()) {
            notificationService.sendBookingConfirmationSms(user, event);
        } else  {
            notificationService.sendBookingConfirmationEmail(user, event);
        }
        return booking;
    }

    @Transactional
    public Booking cancelBooking(String id) {
        Booking booking = bookingRepository.findById(id);
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
        bookingRepository.persistBooking(booking);

        User user = userRepository.findUserById(booking.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + booking.getUserId()));

        if ((user.getEmail() == null || user.getEmail().isEmpty()) && user.getPhone() != null && !user.getPhone().isEmpty()) {
            notificationService.sendBookingCancellationSms(user, event);
        } else {
            notificationService.sendBookingCancellationEmail(user, event);
        }

        return booking;
    }
}
