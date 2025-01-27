package fullstack.util;

import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.service.exception.UserCreationException;

public class Validation {

    public static void validateUserRequest(CreateUserRequest request) throws UserCreationException {
        if (request == null) {
            throw new UserCreationException("La richiesta non può essere vuota. Nome, cognome, password e almeno un contatto sono obbligatori.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new UserCreationException("Nome obbligatorio.");
        }
        if (request.getSurname() == null || request.getSurname().trim().isEmpty()) {
            throw new UserCreationException("Cognome obbligatorio.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new UserCreationException("Password obbligatoria.");
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!Validator.isValidEmail(request.getEmail())) {
                throw new UserCreationException("Email non valida.");
            }
        } else if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().startsWith("+39")) {
                request.setPhone("+39" + request.getPhone());
            }
            if (!Validator.isValidPhone(request.getPhone())) {
                throw new UserCreationException("Numero di telefono non valido.");
            }
        } else {
            throw new UserCreationException("È necessario fornire almeno un'email o un numero di telefono.");
        }
    }

    public static void validateLoginRequest(LoginRequest request) throws IllegalArgumentException {
        if (request == null) {
            throw new IllegalArgumentException("La richiesta di login non può essere nulla.");
        }
        if (request.getEmailOrPhone() == null || request.getEmailOrPhone().isEmpty()) {
            throw new IllegalArgumentException("Email o numero di telefono è obbligatorio.");
        }
        if (request.getEmailOrPhone().matches("\\d+")) {
            if (!request.getEmailOrPhone().startsWith("+39")) {
                request.setEmailOrPhone("+39" + request.getEmailOrPhone());
            }
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La password è obbligatoria.");
        }
    }
}
