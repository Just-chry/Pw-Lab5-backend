package fullstack.rest.resources;

import fullstack.persistence.model.Booking;
import fullstack.rest.model.BookingDetailResponse;
import fullstack.service.BookingService;
import fullstack.service.exception.BookingException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource {
    @Inject
    BookingService bookingService;

    @GET
    public Response getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return Response.ok(bookings).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getBookingById(@PathParam("id") String id) {
        try {
            Booking booking = bookingService.findById(id);
            return Response.ok(booking).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingsByUserSession(@CookieParam("sessionId") String sessionId) {
        try {
            List<Booking> bookings = bookingService.findBySessionId(sessionId);
            return Response.ok(bookings).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error").build();
        }
    }

    @GET
    @Path("/user/details")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserBookingDetails(@CookieParam("sessionId") String sessionId) {
        try {
            List<BookingDetailResponse> bookings = bookingService.getBookingDetailsBySessionId(sessionId);
            return Response.ok(bookings).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error").build();
        }
    }

    @POST
    @Path("/{id}")
    public Response createBooking(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            Booking booking = bookingService.save(sessionId, id);
            return Response.status(Response.Status.CREATED).entity(booking).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        } catch (BookingException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancelBooking(@PathParam("id") String id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return Response.ok(booking).build();
        } catch (UserNotFoundException | BookingException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }
}
