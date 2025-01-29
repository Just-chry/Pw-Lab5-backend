package fullstack.rest.resources;

import fullstack.persistence.model.Role;
import fullstack.service.UserService;
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
    private final UserService userService;

    public TagResource(TagService tagService, TalkService talkService, UserService userService) {
        this.tagService = tagService;
        this.talkService = talkService;
        this.userService = userService;
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
    public Response createTag(@CookieParam("sessionId") String sessionId, Tag tag) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        Tag savedTag = tagService.save(tag);
        return Response.ok(savedTag).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        tagService.delete(id);
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTag(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Tag tag) throws UserNotFoundException {
//        Role userRole = userService.getUserRoleBySessionId(sessionId);
//        if (userRole != Role.admin) {
//            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
//        }
        int updated = tagService.update(id, tag);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Tag not found").build();
        }
        return Response.ok().build();
    }
}