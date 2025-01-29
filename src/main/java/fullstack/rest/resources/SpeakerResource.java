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
    private final UserService userService;

    public SpeakerResource(SpeakerService speakerService, EventService eventService, TalkService talkService, UserService userService) {
        this.speakerService = speakerService;
        this.eventService = eventService;
        this.talkService = talkService;
        this.userService = userService;
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
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        Speaker savedSpeaker = speakerService.save(speaker);
        return Response.ok(savedSpeaker).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        speakerService.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Speaker speaker) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        int updated = speakerService.update(id, speaker);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Speaker not found").build();
        }
        return Response.ok(updated).build();
    }

}
