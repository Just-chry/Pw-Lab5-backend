package fullstack.rest.resources;

import fullstack.persistence.model.User;
import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.rest.model.LoginResponse;
import fullstack.service.AuthenticationService;
import fullstack.service.exception.SessionAlreadyExistsException;
import fullstack.service.exception.UserCreationException;
import fullstack.service.exception.UserNotFoundException;
import fullstack.service.exception.WrongPasswordException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Inject
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("/register")
    public Response register(CreateUserRequest request) throws UserCreationException {
        User user = authenticationService.register(request);
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String verificationLink = "http://localhost:8080/auth/verifyEmail?token=" + user.getTokenEmail() + "&contact=" + user.getEmail();
            authenticationService.sendVerificationEmail(user, verificationLink);
        } else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            authenticationService.sendVerificationSms(user);
        }

        return Response.ok("Registrazione completata con successo, controlla il tuo contatto per confermare.").build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            LoginResponse response = authenticationService.authenticate(request);

            NewCookie sessionCookie = new NewCookie("sessionId", response.getSessionId(), "/", null, "Session Cookie", -1, false);
            return Response.ok(response).cookie(sessionCookie).build();
        } catch (UserNotFoundException | WrongPasswordException | SessionAlreadyExistsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/verifyEmail")
    public Response verifyEmail(@QueryParam("token") String token, @QueryParam("contact") String email) {
        try {
            authenticationService.verifyEmail(token, email);
            return Response.ok("Email verificata con successo.").build();
        } catch (UserCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/verifyPhone")
    public Response verifyPhone(@QueryParam("token") String token, @QueryParam("contact") String phone) {
        try {
            authenticationService.verifyPhone(token, phone);
            return Response.ok("Numero di telefono verificato con successo.").build();
        } catch (UserCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
