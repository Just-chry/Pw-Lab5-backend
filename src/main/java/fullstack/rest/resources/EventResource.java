package fullstack.rest.resources;

import fullstack.persistence.model.Role;
import fullstack.service.UserService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Talk;
import fullstack.service.EventService;
import fullstack.service.SpeakerService;
import fullstack.service.TalkService;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/events")
public class EventResource {

    private final EventService eventService;
    private final TalkService talkService;
    private final SpeakerService speakerService;
    private final UserService userService;

    public EventResource(EventService eventService, TalkService talkService, SpeakerService speakerService, UserService userService) {
        this.eventService = eventService;
        this.talkService = talkService;
        this.speakerService = speakerService;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event getEventById(@PathParam("id") String id) {
        return eventService.findById(id);
    }

    @GET
    @Path("/{id}/talks")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Talk> getTalksByEventId(@PathParam("id") String eventId) {
        return talkService.getTalksByEventId(eventId);
    }

    @GET
    @Path("/{id}/speakers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Speaker> getSpeakersByEventId(@PathParam("id") String eventId) {
        return speakerService.getSpeakersByEventId(eventId);
    }

    @GET
    @Path("date/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEventByDate(@PathParam("date") String date) {
        return eventService.findByDate(date);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(@CookieParam("sessionId") String sessionId, Event event) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        Event savedEvent = eventService.save(event);
        return Response.ok(savedEvent).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        eventService.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Event event) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        int updated = eventService.update(id, event);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found").build();
        }
        return Response.ok().build();
    }
}