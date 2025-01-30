package fullstack.rest.resources;

import fullstack.persistence.model.User;
import fullstack.rest.model.*;
import fullstack.service.UserService;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserCreationException;
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
    @Path("/modify/email")
    public Response modifyEmail(@CookieParam("sessionId") String sessionId, ModifyEmailRequest newEmail) {
        try {
            userService.updateEmail(sessionId, newEmail);
            return Response.ok("Email modificata con successo.").build();
        } catch (UserNotFoundException | UserCreationException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/modify/phone")
    public Response modifyPhone(@CookieParam("sessionId") String sessionId, ModifyPhoneRquest newPhone) {
        try {
            userService.updatePhone(sessionId, newPhone);
            return Response.ok("Telefono modificato con successo.").build();
        } catch (UserNotFoundException | UserCreationException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/modify/name")
    public Response modifyName(@CookieParam("sessionId") String sessionId, ModifyNameRequest newName) {
        try {
            userService.updateName(sessionId, newName);
            return Response.ok("Nome modificato con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/modify/surname")
    public Response modifySurname(@CookieParam("sessionId") String sessionId, ModifySurnameRequest newSurname) {
        try {
            userService.updateSurname(sessionId, newSurname);
            return Response.ok("Cognome modificato con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/modify/password")
    public Response modifyPassword(@CookieParam("sessionId") String sessionId, ModifyPasswordRequest newPassword) {
        try {
            userService.updatePassword(sessionId, newPassword);
            return Response.ok("Password modificata con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (UserCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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

    @POST
    @Path("/forgottenPassword")
    public Response forgottenPassword(ForgottenPasswordRequest request) {
        try {
            userService.forgottenPassword(request.getEmailOrPhone());
            return Response.ok("Codice di verifica inviato con successo.").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/updatePasswordWithCode")
    public Response updatePasswordWithCode(UpdatePasswordWithCodeRequest request) {
        try {
            userService.updatePasswordWithCode(request.getEmailOrPhone(), request.getVerificationCode(), request.getNewPassword(), request.getRepeatNewPassword());
            return Response.ok("Password aggiornata con successo.").build();
        } catch (UserNotFoundException | UserCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}