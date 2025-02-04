package fullstack.rest.resources;

import fullstack.rest.model.CreateTalkRequest;
import fullstack.service.exception.UserNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.SpeakerService;
import fullstack.service.TagService;
import fullstack.service.TalkService;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import org.hibernate.SessionException;

import java.util.List;

@Path("/talks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TalkResource {

    @Inject
    TalkService talkService;
    @Inject
    SpeakerService speakerService;
    @Inject
    TagService tagService;

    public TalkResource(TalkService talkService, SpeakerService speakerService, TagService tagService) {
        this.talkService = talkService;
        this.speakerService = speakerService;
        this.tagService = tagService;
    }

    @GET
    public Response getAllTalks() {
        try {
            List<Talk> talks = talkService.getAllTalks();
            return Response.ok(talks).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTalkById(@PathParam("id") String id) {
        try {
            Talk talk = talkService.findById(id);
            return Response.ok(talk).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/speakers")
    public Response getSpeakersByTalkId(@PathParam("id") String talkId) {
        try {
            List<Speaker> speakers = speakerService.getSpeakerByTalkId(talkId);
            return Response.ok(speakers).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/{id}/tags")
    public Response getTagsByTalkId(@PathParam("id") String talkId) {
        try {
            List<Tag> tags = tagService.getTagsByTalkId(talkId);
            return Response.ok(tags).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createTalk(@CookieParam("sessionId") String sessionId, CreateTalkRequest request) {
        try {
            Talk talk = talkService.save(sessionId, request.getTalk(), request.getTagNames());
            return Response.ok(talk).build();
        } catch (SessionException | UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            talkService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateTalk(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Talk talk) {
        try {
            talkService.updateTalk(sessionId, id, talk);
            return Response.ok().build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}