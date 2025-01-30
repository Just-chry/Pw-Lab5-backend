package fullstack.rest.resources;

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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventService eventService;
    private final TalkService talkService;
    private final SpeakerService speakerService;

    public EventResource(EventService eventService, TalkService talkService, SpeakerService speakerService) {
        this.eventService = eventService;
        this.talkService = talkService;
        this.speakerService = speakerService;
    }

    @GET
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GET
    @Path("/{id}")
    public Event getEventById(@PathParam("id") String id) {
        return eventService.findById(id);
    }

    @GET
    @Path("/{id}/talks")
    public List<Talk> getTalksByEventId(@PathParam("id") String eventId) {
        return talkService.getTalksByEventId(eventId);
    }

    @GET
    @Path("/{id}/speakers")
    public List<Speaker> getSpeakersByEventId(@PathParam("id") String eventId) {
        return speakerService.getSpeakersByEventId(eventId);
    }

    @GET
    @Path("date/{date}")
    public List<Event> getEventByDate(@PathParam("date") String date) {
        return eventService.findByDate(date);
    }

    @POST
    public Response createEvent(@CookieParam("sessionId") String sessionId, Event event) {
        try {
            Event savedEvent = eventService.save(sessionId, event);
            return Response.ok(savedEvent).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            eventService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Event event) {
        try {
            eventService.update(sessionId, id, event);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}