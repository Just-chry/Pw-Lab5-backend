package fullstack.rest.exception;

import fullstack.service.exception.AdminAccessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AdminAccessExceptionMapper implements ExceptionMapper<AdminAccessException> {
    @Override
    public Response toResponse(AdminAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(exception.getMessage())
                .build();
    }
}
