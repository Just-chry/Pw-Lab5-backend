package fullstack.rest.exception;

import fullstack.service.exception.UnauthorizedAccessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedAccessExceptionMapper implements ExceptionMapper<UnauthorizedAccessException> {

    @Override
    public Response toResponse(UnauthorizedAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(exception.getMessage())
                .build();
    }
}