package fullstack.rest.resources;

import fullstack.persistence.model.Booking;
import fullstack.service.BookingService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            Booking booking = bookingService.save(sessionId, id);
            return Response.status(Response.Status.CREATED).entity(booking).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Booking confirmBooking(@PathParam("id") String id) {
        //admin
        return bookingService.confirmBooking(id);
    }

    @PUT
    @Path("/{id}/decline")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Booking declineBooking(@PathParam("id") String id) {
        //admin
        return bookingService.declineBooking(id);
    }

    @PUT
    @Path("/{id}/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Booking cancelBooking(@PathParam("id") String id) {
        return bookingService.cancelBooking(id);
    }
}
