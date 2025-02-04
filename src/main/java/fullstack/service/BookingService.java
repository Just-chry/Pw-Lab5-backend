package fullstack.service;

import fullstack.persistence.model.*;
import fullstack.persistence.repository.BookingRepository;
import fullstack.persistence.repository.TicketRepository;
import fullstack.persistence.repository.UserRepository;
import fullstack.service.exception.BookingException;
import fullstack.service.exception.UserNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;

import java.util.List;

import static fullstack.util.Messages.*;

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
    @Inject
    TicketRepository ticketRepository;

    public List<Booking> getAllBookings() throws NoContentException {
        List<Booking> bookings = bookingRepository.listAll();
        if (bookings.isEmpty()) {
            throw new NoContentException(BOOKING_NOT_FOUND);
        }
        return bookings;
    }

    public Booking findById(String id) throws NoContentException {
        Booking booking = bookingRepository.findById(id);
        if (booking == null) {
            throw new NoContentException(BOOKING_NOT_FOUND);
        }
        return booking;
    }

    @Transactional
    public Booking save(String sessionId, String eventId) throws UserNotFoundException, NoContentException, BookingException {
        User user = userService.getUserBySessionId(sessionId);
        String userId = user.getId();
        Event event = eventService.findById(eventId);

        if (event == null) {
            throw new RuntimeException(EVENT_NOT_FOUND + eventId);
        }

        if (bookingRepository.findExistingBooking(userId, eventId) != null) {
            throw new BookingException(BOOKING_EXISTS);
        }

        Ticket ticket = ticketRepository.findAvailableTicketForEvent(eventId);
        if (ticket == null) {
            throw new BookingException(BOOKING_FULL);
        }

        ticket.setUserId(userId);
        ticketRepository.persist(ticket);

        Booking booking = bookingRepository.createBooking(userId, eventId);

        event.setParticipantsCount(event.getParticipantsCount() + 1);

        if (user.getEmail() == null || user.getEmail().isEmpty() && user.getPhone() != null && !user.getPhone().isEmpty()) {
            notificationService.sendBookingConfirmationSms(user, event);
        } else {
            notificationService.sendBookingConfirmationEmail(user, event);
        }

        return booking;
    }

    @Transactional
    public Booking cancelBooking(String id) throws UserNotFoundException, BookingException, NoContentException {
        Booking booking = bookingRepository.findById(id);
        if (booking == null) {
            throw new BookingException(BOOKING_NOT_FOUND + id);
        }

        Event event = eventService.findById(booking.getEventId());
        if (event == null) {
            throw new IllegalArgumentException(EVENT_NOT_FOUND + booking.getEventId());
        }

        if (booking.getStatus() == Status.CONFIRMED) {
            event.setParticipantsCount(event.getParticipantsCount() - 1);
            eventService.persist(event);
        }

        booking.setStatus(Status.CANCELED);
        bookingRepository.persistBooking(booking);

        User user = userRepository.findUserById(booking.getUserId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + booking.getUserId()));

        if ((user.getEmail() == null || user.getEmail().isEmpty()) && user.getPhone() != null && !user.getPhone().isEmpty()) {
            notificationService.sendBookingCancellationSms(user, event);
        } else {
            notificationService.sendBookingCancellationEmail(user, event);
        }

        return booking;
    }
}
