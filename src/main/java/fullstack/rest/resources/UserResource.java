package fullstack.rest.resources;

import fullstack.persistence.model.User;
import fullstack.service.UserService;
import fullstack.service.exception.UserNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") String userId) {
        try {
            return Response.ok(userService.getUserById(userId)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{userId}/profile")
    public Response updateProfile(@PathParam("userId") String userId, User updatedUser) {
        try {
            userService.updateProfile(userId, updatedUser);
            return Response.ok("Profilo aggiornato. Verifica il nuovo contatto se modificato.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            userService.deleteUser(userId);
            return Response.ok("Utente eliminato con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response listUsers() {
        List<User> users = userService.listUsers();
        return Response.ok(users).build();
    }

    @PUT
    @Path("/{userId}/promote")
    public Response promoteUserToAdmin(@PathParam("userId") String userId) {
        try {
            userService.promoteUserToAdmin(userId);
            return Response.ok("Utente promosso ad admin con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

}