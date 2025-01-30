package fullstack.rest.resources;

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

    public TalkResource(TalkService talkService, SpeakerService speakerService, TagService tagService) {
        this.talkService = talkService;
        this.speakerService = speakerService;
        this.tagService = tagService;
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
    public Response createTalk(@CookieParam("sessionId") String sessionId, Talk talk) {
        try {
            Talk savedTalk = talkService.save(sessionId, talk);
            return Response.ok(savedTalk).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id)  {
        try {
            talkService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Talk talk) {
        try {
            int updated = talkService.updateTalk(sessionId, id, talk);
            if (updated == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Talk not found").build();
            }
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}