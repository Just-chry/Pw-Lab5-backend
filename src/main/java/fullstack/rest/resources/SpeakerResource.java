package fullstack.rest.resources;

import fullstack.service.exception.UserNotFoundException;
import jakarta.inject.Inject;
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

@Path("/speakers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SpeakerResource {

    @Inject
    SpeakerService speakerService;
    @Inject
    EventService eventService;
    @Inject
    TalkService talkService;

    @GET
    public Response getAllSpeakers() {
        try {
            List<Speaker> speakers = speakerService.getAllSpeakers();
            return Response.ok(speakers).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getSpeakerById(@PathParam("id") String id) {
        try {
            Speaker speaker = speakerService.findById(id);
            return Response.ok(speaker).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/events")
    public Response getEventsBySpeakerId(@PathParam("id") String speakerId) {
        try {
            List<Event> events = eventService.getEventsBySpeakerId(speakerId);
            return Response.ok(events).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/talks")
    public Response getSpeakersByTalkId(@PathParam("id") String speakerId) {
        try {
            List<Talk> talks = talkService.getTalksBySpeakerId(speakerId);
            return Response.ok(talks).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }


    @POST
    public Response createSpeaker(@CookieParam("sessionId") String sessionId, Speaker speaker) throws UserNotFoundException {
        try {
            Speaker savedSpeaker = speakerService.save(sessionId, speaker);
            return Response.ok(savedSpeaker).build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            speakerService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Speaker speaker) {
        try {
            speakerService.update(sessionId, id, speaker);
            return Response.ok().build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
