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
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import org.hibernate.SessionException;

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
    public Response getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return Response.ok(events).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") String id) {
        try {
            Event event = eventService.findById(id);
            return Response.ok(event).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/talks")
    public Response getTalksByEventId(@PathParam("id") String eventId) {
        try {
            List<Talk> talks = talkService.getTalksByEventId(eventId);
            return Response.ok(talks).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/speakers")
    public Response getSpeakersByEventId(@PathParam("id") String eventId) {
        try {
            List<Speaker> speakers = speakerService.getSpeakersByEventId(eventId);
            return Response.ok(speakers).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("date/{date}")
    public Response getEventByDate(@PathParam("date") String date) {
        try {
            List<Event> events = eventService.findByDate(date);
            return Response.ok(events).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createEvent(@CookieParam("sessionId") String sessionId, EventRequest eventRequest) {
        try {
            Event savedEvent = eventService.save(sessionId, eventRequest.getEvent(), eventRequest.getTalks());
            return Response.ok(savedEvent).build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            eventService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEvent(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Event event) {
        try {
            eventService.update(sessionId, id, event);
            return Response.ok().build();
        } catch (SessionException | NoContentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}