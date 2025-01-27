package fullstack.util;

import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.service.exception.UserCreationException;

public class Validation {

    public static void validateUserRequest(CreateUserRequest request) throws UserCreationException {
        if (request == null) {
            throw new UserCreationException("La richiesta non può essere vuota. Nome, cognome, password e almeno un contatto sono obbligatori.");
        }
        if (!request.hasValidNameAndSurname()) {
            throw new UserCreationException("Nome e cognome sono obbligatori.");
        }
        if (!request.hasValidContact()) {
            throw new UserCreationException("È necessario inserire almeno un'email o un numero di telefono.");
        }
    }

    public static void validateLoginRequest(LoginRequest request) throws IllegalArgumentException {
        if (request == null) {
            throw new IllegalArgumentException("La richiesta di login non può essere nulla.");
        }
        if (request.getEmailOrPhone() == null || request.getEmailOrPhone().isEmpty()) {
            throw new IllegalArgumentException("Email o numero di telefono è obbligatorio.");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La password è obbligatoria.");
        }
    }
}
