package fullstack.rest.resources;

import fullstack.persistence.model.Booking;
import fullstack.service.BookingService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/bookings")
public class BookingResource {
    private BookingService bookingService;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GET
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GET
    @Path("/{id}")
    public Booking getBookingById(@PathParam("id") String id) {
        return bookingService.findById(id);
    }

    @POST
    public Booking createBooking(@CookieParam("sessionId") String sessionId, Booking booking) throws UserNotFoundException {
        return bookingService.save(booking, sessionId);
    }

    @DELETE
    @Path("/{id}")
    public void deleteBooking(@PathParam("id") String id) {
        bookingService.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Booking updateBooking(@PathParam("id") String id, Booking booking) {

        return bookingService.update(id, booking);
    }
}
