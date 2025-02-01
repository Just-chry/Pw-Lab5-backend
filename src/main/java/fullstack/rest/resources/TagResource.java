package fullstack.rest.resources;

import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.TagService;
import fullstack.service.TalkService;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import org.hibernate.SessionException;

import java.util.List;

@Path("/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {
    private final TagService tagService;
    private final TalkService talkService;

    public TagResource(TagService tagService, TalkService talkService) {
        this.tagService = tagService;
        this.talkService = talkService;
    }

    @GET
    public Response getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return Response.ok(tags).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTagById(@PathParam("id") String id) {
        try {
            Tag tag = tagService.findById(id);
            return Response.ok(tag).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/talks")
    public Response getTagsByTalkId(@PathParam("id") String tagId) {
        try {
            List<Talk> tags = talkService.getTagsByTalkId(tagId);
            return Response.ok(tags).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createTag(@CookieParam("sessionId") String sessionId, Tag tag) {
        try {
            Tag savedTag = tagService.save(sessionId, tag);
            return Response.ok(savedTag).build();
        } catch (SessionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            tagService.delete(sessionId, id);
            return Response.ok().build();
        } catch (SessionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Tag tag) {
        try {
            tagService.update(sessionId, id, tag);
            return Response.ok().build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }

    }
}