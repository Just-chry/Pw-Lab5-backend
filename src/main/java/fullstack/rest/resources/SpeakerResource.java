package fullstack.rest.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Speaker;
import fullstack.persistence.model.Talk;
import fullstack.service.EventService;
import fullstack.service.SpeakerService;
import fullstack.service.TalkService;

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
    public Speaker createSpeaker(Speaker speaker) {
        return speakerService.save(speaker);
    }

    @DELETE
    @Path("/{id}")
    public void deleteSpeaker(@PathParam("id") String id) {
        speakerService.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int updateSpeaker(@PathParam("id") String id, Speaker speaker) {
        return speakerService.update(id, speaker);
    }
}
