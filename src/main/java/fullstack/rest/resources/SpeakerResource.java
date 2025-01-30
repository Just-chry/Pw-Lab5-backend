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

@Path("/speakers")
public class SpeakerResource {
    private final SpeakerService speakerService;
    private final EventService eventService;
    private final TalkService talkService;

    public SpeakerResource(SpeakerService speakerService, EventService eventService, TalkService talkService) {
        this.speakerService = speakerService;
        this.eventService = eventService;
        this.talkService = talkService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Speaker> getAllSpeakers() {
        return speakerService.getAllSpeakers();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Speaker getSpeakerById(@PathParam("id") String id) {
        return speakerService.findById(id);
    }

    @GET
    @Path("/{id}/events")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEventsBySpeakerId(@PathParam("id") String speakerId) {
        return eventService.getEventsBySpeakerId(speakerId);
    }

    @GET
    @Path("/{id}/talks")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Talk> getSpeakersByTalkId(@PathParam("id") String speakerId) {
        return talkService.getTalksBySpeakerId(speakerId);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSpeaker(@CookieParam("sessionId") String sessionId, Speaker speaker) throws UserNotFoundException {
        try {
            Speaker savedSpeaker = speakerService.save(sessionId, speaker);
            return Response.ok(savedSpeaker).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            speakerService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Speaker speaker)  {
        try {
        int updated = speakerService.update(sessionId, id, speaker);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Speaker not found").build();
        }
        return Response.ok(updated).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
