package fullstack.rest.resources;

import fullstack.persistence.model.Role;
import fullstack.service.UserService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.SpeakerService;
import fullstack.service.TagService;
import fullstack.service.TalkService;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/talks")
public class TalkResource {
    private final TalkService talkService;
    private final SpeakerService speakerService;
    private final TagService tagService;
    private final UserService userService;

    public TalkResource(TalkService talkService, SpeakerService speakerService, TagService tagService, UserService userService) {
        this.talkService = talkService;
        this.speakerService = speakerService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Talk> getAllTalks() {
        return talkService.getAllTalks();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Talk getTalkById(@PathParam("id") String id) {
        return talkService.findById(id);
    }

    @GET
    @Path("/{id}/speakers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Speaker> getSpeakersByTalkId(@PathParam("id") String talkId) {
        return speakerService.getSpeakerByTalkId(talkId);
    }

    @GET
    @Path("/{id}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tag> getTagsByTalkId(@PathParam("id") String talkId) {
        return tagService.getTagsByTalkId(talkId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTalk(@CookieParam("sessionId") String sessionId, Talk talk) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        Talk savedTalk = talkService.save(talk);
        return Response.ok(savedTalk).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole!= Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        talkService.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Talk talk) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        int updated = talkService.update(id, talk);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Talk not found").build();
        }
        return Response.ok().build();
    }
}