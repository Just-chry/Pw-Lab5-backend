package fullstack.rest.resources;

import fullstack.service.UserService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Partner;
import fullstack.service.EventService;
import fullstack.service.PartnerService;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import org.hibernate.SessionException;

import java.util.List;

@Path("/partners")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PartnerResource {
    private final PartnerService partnerService;
    private final EventService eventService;

    public PartnerResource(PartnerService partnerService, EventService eventService) {
        this.partnerService = partnerService;
        this.eventService = eventService;
    }

    @GET
    public Response getAllPartners() {
        try {
            List<Partner> partners = partnerService.getAllPartners();
            return Response.ok(partners).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getPartnerById(@PathParam("id") String id) {
        try {
            Partner partner = partnerService.findById(id);
            return Response.ok(partner).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/events")
    public Response getEventsByPartnerId(@PathParam("id") String partnerId) {
        try {
            List<Event> events = eventService.getEventsByPartnerId(partnerId);
            return Response.ok(events).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/value/{value}")
    public Response getPartnerByName(@PathParam("value") String value) {
        try {
            List<Partner> partners = partnerService.findByValue(value);
            return Response.ok(partners).build();
        } catch (NoContentException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createPartner(@CookieParam("sessionId") String sessionId, Partner partner) {
        try {
            Partner savedPartner = partnerService.save(sessionId, partner);
            return Response.ok(savedPartner).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePartner(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) {
        try {
            partnerService.deleteById(sessionId, id);
            return Response.noContent().build();
        } catch (SessionException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updatePartner(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Partner partner) {
        try {
            int updated = partnerService.update(sessionId, id, partner);
            return Response.ok(updated).build();
        } catch (SessionException | NoContentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
