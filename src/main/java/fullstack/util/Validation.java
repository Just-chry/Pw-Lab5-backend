package fullstack.util;

import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.service.exception.UserCreationException;

public class Validation {

    public static void validateUserRequest(CreateUserRequest request) throws UserCreationException {
        if (request == null) {
            throw new UserCreationException(ErrorMessages.CONTACT_REQUIRED);
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new UserCreationException(ErrorMessages.NAME_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new UserCreationException(ErrorMessages.PASSWORD_REQUIRED);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!ContactValidator.isValidEmail(request.getEmail())) {
                throw new UserCreationException(ErrorMessages.INVALID_EMAIL);
            }
        } else if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            request.setPhone(ContactValidator.formatPhone(request.getPhone()));
            if (!ContactValidator.isValidPhone(request.getPhone())) {
                throw new UserCreationException(ErrorMessages.INVALID_PHONE);
            }
        } else {
            throw new UserCreationException(ErrorMessages.CONTACT_REQUIRED);
        }
    }

    public static void validateLoginRequest(LoginRequest request) throws IllegalArgumentException {
        if (request == null) {
            throw new IllegalArgumentException("È necessario fornire almeno un'email o un numero di telefono.");
        }
        if ((request.getEmail() == null || request.getEmail().isEmpty()) &&
                (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
            throw new IllegalArgumentException("È necessario fornire almeno un'email o un numero di telefono.");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La password è obbligatoria.");
        }
    }
}
