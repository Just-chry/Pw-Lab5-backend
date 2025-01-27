package fullstack.rest.exception;

import fullstack.service.exception.UnAuthorizedAccessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnAuthorizedAccessExceptionMapper implements ExceptionMapper<UnAuthorizedAccessException> {

    @Override
    public Response toResponse(UnAuthorizedAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(exception.getMessage())
                .build();
    }
}