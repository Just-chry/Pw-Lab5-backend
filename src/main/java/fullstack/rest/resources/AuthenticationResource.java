package fullstack.rest.resources;

import fullstack.persistence.model.User;
import fullstack.rest.model.CreateUserRequest;
import fullstack.service.AuthenticationService;
import fullstack.service.exception.UserCreationException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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

        return Response.ok("Registrazione completata con successo, controlla il tuo contatto per confermare.").build();
    }
}
