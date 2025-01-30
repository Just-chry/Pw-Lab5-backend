package fullstack.rest.resources;

import fullstack.rest.model.EventRequest;
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

    public EventResource(EventService eventService, TalkService talkService, SpeakerService speakerService) {
        this.eventService = eventService;
        this.talkService = talkService;
        this.speakerService = speakerService;
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
    public Response createEvent(@CookieParam("sessionId") String sessionId, EventRequest eventRequest) {
        try {
            Event savedEvent = eventService.save(sessionId, eventRequest.getEvent(), eventRequest.getTalks());
            return Response.ok(savedEvent).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Event event)  {
        try {
        int updated = eventService.update(sessionId, id, event);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found").build();
        }
        return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}