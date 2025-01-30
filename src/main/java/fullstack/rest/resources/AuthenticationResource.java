package fullstack.rest.resources;

import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.rest.model.LoginResponse;
import fullstack.service.AuthenticationService;
import fullstack.service.exception.*;
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
        authenticationService.register(request);
        return Response.ok("Registrazione completata con successo, controlla il tuo contatto per confermare.").build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            LoginResponse response = authenticationService.authenticate(request, request.getRememberMe());
            NewCookie sessionCookie = new NewCookie("sessionId", response.getSessionId(), "/", null, "Session Cookie", -1, true, true);
            return Response.ok(response).cookie(sessionCookie).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/logout")
    public Response logout(@CookieParam("sessionId") String sessionId) throws UserSessionNotFoundException {
        authenticationService.logout(sessionId);
        NewCookie expiredCookie = new NewCookie("sessionId", "", "/", null, "Session Cookie", -1, true, true);
        return Response.ok("Logout avvenuto con successo").cookie(expiredCookie).build();
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
