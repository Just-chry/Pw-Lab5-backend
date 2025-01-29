package fullstack.rest.resources;

import fullstack.persistence.model.User;
import fullstack.rest.model.AdminResponse;
import fullstack.service.UserService;
import fullstack.service.exception.AdminAccessException;
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
            return Response.ok(userService.getUserResponseById(userId)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{userId}/modify/email")
    public Response modifyEmail(@PathParam("userId") String userId, String newEmail) {
        try {
            userService.updateEmail(userId, newEmail);
            return Response.ok("Email modificata con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{userId}/modify/password")
    public Response modifyPassword() {

    }

    @PUT
    @Path("/{userId}/modify/phone")
    public Response modifyPassword() {

    }

    @PUT
    @Path("/{userId}/modify/name")
    public Response modifyPassword() {

    }

    @PUT
    @Path("/{userId}/modify/surname")
    public Response modifyPassword() {

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
    public Response listUsers(@CookieParam("sessionId") String sessionId) {
        try {
            List<AdminResponse> users = userService.listUsers(sessionId);
            return Response.ok(users).build();
        } catch (AdminAccessException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/{userId}/promote")
    public Response promoteUserToAdmin(@CookieParam("sessionId") String sessionId, @PathParam("userId") String userId) {
        try {
            userService.promoteUserToAdmin(userId, sessionId);
            return Response.ok("Utente promosso ad admin con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

}