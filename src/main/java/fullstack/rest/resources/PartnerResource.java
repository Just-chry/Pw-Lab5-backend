package fullstack.rest.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Partner;
import fullstack.service.EventService;
import fullstack.service.PartnerService;

import java.util.List;

@Path("/partners")
public class PartnerResource {
    private final PartnerService partnerService;
    private final EventService eventService;

    public PartnerResource(PartnerService partnerService, EventService eventService) {
        this.partnerService = partnerService;
        this.eventService = eventService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partner> getAllPartners() {
        return partnerService.getAllPartners();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Partner getPartnerById(@PathParam("id") String id) {
        return partnerService.findById(id);
    }

    @GET
    @Path("/{id}/events")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEventsByPartnerId(@PathParam("id") String partnerId) {
        return eventService.getEventsByPartnerId(partnerId);
    }

    @GET
    @Path("/value/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partner> getPartnerByName(@PathParam("value") String value) {
        return partnerService.findByValue(value);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Partner createPartner(Partner partner) {
        return partnerService.save(partner);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int updateSpeaker(@PathParam("id") String id, Partner partner) {
        return partnerService.update(id, partner);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Partner deletePartner(@PathParam("id") String id) {
        return partnerService.deleteById(id);
    }
}
