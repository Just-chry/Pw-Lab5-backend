package fullstack.rest.resources;

import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.TagService;
import fullstack.service.TalkService;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tags")
public class TagResource {
    private final TagService tagService;
    private final TalkService talkService;

    public TagResource(TagService tagService, TalkService talkService) {
        this.tagService = tagService;
        this.talkService = talkService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Tag getTagById(@PathParam("id") String id) {
        return tagService.findById(id);
    }

    @GET
    @Path("/{id}/talks")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Talk> getTagsByTalkId(@PathParam("id") String tagId) {
        return talkService.getTagsByTalkId(tagId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTag(@CookieParam("sessionId") String sessionId, Tag tag) {
        try {
            Tag savedTag = tagService.save(sessionId, tag);
            return Response.ok(savedTag).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            tagService.delete(sessionId, id);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Tag tag)  {
        try {
        int updated = tagService.update(sessionId, id, tag);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Tag not found").build();
        }
        return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Utente non trovato").build();
        }

    }
}