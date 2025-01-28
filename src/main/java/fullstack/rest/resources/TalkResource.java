package fullstack.rest.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Tag;
import fullstack.persistence.model.Talk;
import fullstack.service.SpeakerService;
import fullstack.service.TagService;
import fullstack.service.TalkService;

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
    public Talk createTalk(Talk talk) {
        return talkService.save(talk);
    }

    @DELETE
    @Path("/{id}")
    public void deleteTalk(@PathParam("id") String id) {
        talkService.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int updateTalk(@PathParam("id") String id, Talk talk) {
        return talkService.update(id, talk);
    }
}