package fullstack.rest.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.TagService;
import fullstack.service.TalkService;

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
    public Tag createTag(Tag tag) {
        return tagService.save(tag);
    }

    @DELETE
    @Path("/{id}")
    public void deleteTag(@PathParam("id") String id) {
        tagService.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int updateTag(@PathParam("id") String id, Tag tag) {
        return tagService.update(id, tag);
    }
}