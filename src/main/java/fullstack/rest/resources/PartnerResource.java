package fullstack.rest.resources;

import fullstack.persistence.model.Role;
import fullstack.service.UserService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fullstack.persistence.model.Event;
import fullstack.persistence.model.Partner;
import fullstack.service.EventService;
import fullstack.service.PartnerService;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/partners")
public class PartnerResource {
    private final PartnerService partnerService;
    private final EventService eventService;
    private final UserService userService;

    public PartnerResource(PartnerService partnerService, EventService eventService, UserService userService) {
        this.partnerService = partnerService;
        this.eventService = eventService;
        this.userService = userService;
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
    public Response createPartner(@CookieParam("sessionId") String sessionId, Partner partner) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        Partner savedPartner = partnerService.save(partner);
        return Response.ok(savedPartner).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePartner(@CookieParam("sessionId") String sessionId, @PathParam("id") String id) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        partnerService.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpeaker(@CookieParam("sessionId") String sessionId, @PathParam("id") String id, Partner partner) throws UserNotFoundException {
        Role userRole = userService.getUserRoleBySessionId(sessionId);
        if (userRole != Role.admin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
        }
        int updated = partnerService.update(id, partner);
        if (updated == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Partner not found").build();
        }
        return Response.ok(updated).build();
    }
}
